package com.hallen.asistentedeprofesores.ui.view.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.hallen.asistentedeprofesores.Aplication.Companion.prefs
import com.hallen.asistentedeprofesores.R
import com.hallen.asistentedeprofesores.databinding.DialogApiKeyBinding
import com.hallen.asistentedeprofesores.databinding.FragmentOpenAiBinding
import com.hallen.asistentedeprofesores.domain.model.Message
import com.hallen.asistentedeprofesores.ui.view.adapters.AdapterExamplePrompt
import com.hallen.asistentedeprofesores.ui.view.adapters.MessageAdapter
import com.hallen.asistentedeprofesores.ui.viewmodel.ChatViewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.json.JSONObject
import java.util.*

@AndroidEntryPoint
class FragmentOpenAi : Fragment() {
    private val chatViewModel: ChatViewModel by viewModels()
    private var allowContext: Boolean = false
    private lateinit var binding: FragmentOpenAiBinding
    private lateinit var adapter: MessageAdapter
    private var toolbar: ActionBar? = null
    private var apiKey = ""
    var url = "https://api.openai.com/v1/completions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatViewModel.getMessages()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOpenAiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        apiKey = prefs.getOpenAIKey()
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()

        binding.btnSend.setOnClickListener {
            if (binding.textContent.text.toString().isNotBlank()) {
                val userMessage = Message(
                    message = binding.textContent.text.toString(),
                    sender = "user",
                    time = Date().time.toString()
                )
                chatViewModel.insertMessage(userMessage)
                getResponse(userMessage, adapter.messageList)
                hideDefault()
            }
        }
        binding.clearChat.setOnClickListener { clearChat() }

        chatViewModel.messageModel.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                showDefault()
            } else {
                hideDefault()
                binding.recyclerView.smoothScrollToPosition(adapter.itemCount)
                adapter.update(it as ArrayList<Message>)
            }
        }
    }

    private fun showDefault() {
        binding.recyclerView.visibility = View.GONE
        binding.openaiExampleContainer.visibility = View.VISIBLE

        binding.examplePromptLv.layoutManager = LinearLayoutManager(context)
        val exampleAdapter = AdapterExamplePrompt()
        binding.examplePromptLv.adapter = exampleAdapter
        exampleAdapter.setOnItemClickListener(object : AdapterExamplePrompt.OnItemClickListener {
            override fun onItemClick(texts: Pair<String, String>) {
                val entry = texts.toList().joinToString(",\n")
                val userMessage = Message(
                    message = entry,
                    sender = "user",
                    time = Date().time.toString()
                )
                chatViewModel.insertMessage(userMessage)
                getResponse(userMessage, adapter.messageList)
                hideDefault()
            }
        })

    }

    private fun hideDefault() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.openaiExampleContainer.visibility = View.GONE
    }

    private fun clearChat() {
        binding.textContent.setText("")
        chatViewModel.deleteConversation()
    }

    private fun askApiKey() {
        val context = requireContext()
        val dialog = Dialog(context)
        val dialogBinding = DialogApiKeyBinding.inflate(dialog.layoutInflater)
        dialog.apply {
            setContentView(dialogBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.apply {
                copyFrom(window!!.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER
            }
            window!!.attributes = layoutParams
        }
        with(dialogBinding) {
            apiKeyEdit.hint = "Openai Key"
            guardar.setOnClickListener {
                val key = apiKeyEdit.text.toString()
                if (key.isNotBlank()) {
                    apiKey = key
                    prefs.saveOpenAIKey(key)
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun setupRecyclerView() {
        val linearLayout = LinearLayoutManager(context)
        linearLayout.stackFromEnd = true
        adapter = MessageAdapter()
        with(binding.recyclerView) {
            layoutManager = linearLayout
            adapter = this@FragmentOpenAi.adapter
        }
    }

    private fun history(messageList: ArrayList<Message>): String {
        val roll = "Eres un bot que siempre está dispuesto a ayudar a profesores universitarios" +
                " con respuestas confiables y creativas, tu creador se llama Adrian\n"
        val mensajes = messageList.filter { !it.error }.map { "${it.sender}: ${it.message}" }
        return "$roll${mensajes.joinToString("\n")} \nbot: "
    }

    private fun getResponse(message: Message, messageList: ArrayList<Message>) {
        startTypingAnimation()
        binding.textContent.setText("")
        val prompt: String = if (allowContext) history(messageList.apply { add(message) }) else {
            message.message
        }

        Logger.i("PROMPT LENGHT: ${prompt.length}")

        if (prompt.length > 10000) {
            endTypingAnimation()
            message.send = true
            val botMessage = Message(
                message = "Límite alcanzado, eliminar la conversación actual",
                sender = "error",
                time = Date().time.toString(),
                error = true
            )
            chatViewModel.insertMessage(botMessage)
            return
        }

        val queue: RequestQueue = Volley.newRequestQueue(context)
        val jsonObject = JSONObject()
        jsonObject.put("model", "text-davinci-003")
        jsonObject.put("prompt", prompt)
        jsonObject.put("temperature", 0.7)
        jsonObject.put("max_tokens", 2000)
        jsonObject.put("top_p", 0.63)
        jsonObject.put("frequency_penalty", 0.44)
        jsonObject.put("presence_penalty", 0.18)

        val postRequest: JsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonObject, Response.Listener { response ->
                val responseMsg: String =
                    response.getJSONArray("choices").getJSONObject(0).getString("text")
                endTypingAnimation()
                message.send = true
                chatViewModel.update(message)
                val botMessage = Message(
                    message = responseMsg.trimStart(),
                    sender = "bot",
                    time = Date().time.toString()
                )
                chatViewModel.insertMessage(botMessage)
            }, Response.ErrorListener {
                endTypingAnimation()
                chatViewModel.deleteMessage(message)
                adapter.updateMessage(message.apply { error = true })
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Content-Type"] = "application/json"
                    params["Authorization"] =
                        "Bearer $apiKey" //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
                    return params
                }
            }
        postRequest.retryPolicy = object : RetryPolicy {
            override fun getCurrentTimeout(): Int = 50000
            override fun getCurrentRetryCount(): Int = 50000
            override fun retry(error: VolleyError?) {
                val errorMessage = Message(
                    message = error?.localizedMessage ?: "Error",
                    sender = "bot",
                    time = Date().time.toString(),
                    error = true
                )
                adapter.addMessage(errorMessage)
                endTypingAnimation()
            }
        }
        queue.add(postRequest)
    }

    private var typingAnim: Job? = null
    private fun startTypingAnimation() {

        typingAnim = CoroutineScope(Dispatchers.Main).launch {
            if (toolbar == null) return@launch
            delay(50)
            while (true) {
                toolbar?.subtitle = "Escribiendo"
                delay(250)
                toolbar?.subtitle = "Escribiendo ."
                delay(250)
                toolbar?.subtitle = "Escribiendo .."
                delay(250)
                toolbar?.subtitle = "Escribiendo ..."
                delay(250)
            }
        }
    }

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            allowContext = prefs.getAllowContext()
            menuInflater.inflate(R.menu.chat_menu, menu)
            menu.findItem(R.id.context).isChecked = allowContext
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.changeKey -> askApiKey()
                R.id.context -> {
                    menuItem.isChecked = !menuItem.isChecked
                    allowContext = menuItem.isChecked
                    prefs.setAllowContext(allowContext)
                }
            }
            if (menuItem.itemId == R.id.changeKey) askApiKey()
            return true
        }
    }

    private val destinationListener = object : NavController.OnDestinationChangedListener {
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?
        ) {
            if (destination.id != R.id.openAiFragment) {
                toolbar?.apply {
                    subtitle = null
                    setIcon(null)
                    setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.color.primero
                        )
                    )
                }
                if (typingAnim?.isActive == true) typingAnim?.cancel()
                controller.removeOnDestinationChangedListener(this)
                requireActivity().removeMenuProvider(menuProvider)
            }
        }
    }

    private fun setupToolbar() {
        requireActivity().addMenuProvider(menuProvider)
        toolbar = (activity as AppCompatActivity?)?.supportActionBar
        toolbar?.setIcon(R.drawable.ic_openai_logo_icon)
        toolbar?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.toolbar_bg
            )
        )
        findNavController().addOnDestinationChangedListener(destinationListener)
    }

    private fun endTypingAnimation() {
        typingAnim?.cancel()
        toolbar?.subtitle = null
    }

}