package com.rifaqat.notesmaker.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.rifaqat.notesmaker.MainActivity
import com.rifaqat.notesmaker.R
import com.rifaqat.notesmaker.adapter.NotesAdapter
import com.rifaqat.notesmaker.databinding.FragmentNoteBinding
import com.rifaqat.notesmaker.utils.SwipeToDelete
import com.rifaqat.notesmaker.utils.hideKeyboard
import com.rifaqat.notesmaker.viewmodels.NotesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding

    private lateinit var notesAdapter: NotesAdapter

    /*
      the activityViewModels() delegate from the androidx.fragment.app.activityViewModels package
      to initialize the viewModel variable with the shared ViewModel associated with the Activity.
      This is a correct approach if you want to share the same ViewModel instance across multiple
      Fragments within the same Activity. */
    private val viewModel: NotesViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*custom exit and enter transitions using MaterialElevationScale transitions in the onCreate()
         method. These transitions will provide a smooth elevation change animation during fragment
         transitions.*/
        exitTransition = MaterialElevationScale(false).apply {
            duration = 350
        }
        enterTransition = MaterialElevationScale(true).apply {
            duration = 350
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment.data binding initialization
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note, container, false)

        //        On back pressed we want to close the app.
        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle back button press here
                requireActivity().finish() // This will exit the app when back button is pressed
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Accessing MainActivity
        val activity = activity as MainActivity

        //Find nav controller.
        val navController = Navigation.findNavController(view)

        //calling function from extension.kt in utils to hide keyboard.
        requireView().hideKeyboard()

        /*
         the code creates a coroutine on the main thread and uses a delay of 10 milliseconds before
         changing the status bar color. The delay is a non-blocking operation and allows the UI to
         continue functioning smoothly while the coroutine is paused. Then, it sets the status bar
         color to transparent, removes the background drawing flag, and finally, sets the status bar
         color to a gray shade.*/
        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
//            activity.window.statusBarColor = Color.TRANSPARENT
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor = Color.parseColor("#9E9D9D")
        }

//        Below two function are for fab button click to add new note.
        binding.addNoteFab.setOnClickListener {
            binding.appBar.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragment2ToSaveOrUpdateFragment22())
        }
        binding.innerFab.setOnClickListener {
            binding.appBar.visibility = View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragment2ToSaveOrUpdateFragment22())
        }

//        call the method for recyclerView work
        recyclerViewDisplay()

//        Here we are calling swipe to delete function
        swipeToDelete(binding.rvNotes)

//        Here is search functionality
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.noData.isVisible = false
            }

            override fun onTextChanged(search: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (search.toString().isNotEmpty()) {
                    val text = search.toString()
                    val query = "%$text%"
                    if (query.isNotEmpty()) {
                        viewModel.search(query).observe(viewLifecycleOwner) { list ->
                            notesAdapter.submitList(list)
                        }
                    } else {
                        observeDataChanges()
                    }
                } else {
                    observeDataChanges()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.search.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                view.clearFocus()
                requireView().hideKeyboard()
            }
            return@setOnEditorActionListener true
        }

//        We want to hide text with button whenever a user start scrolling so here is the code
        binding.rvNotes.setOnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->
            when {
                scrollY > oldScrollY -> {
                    binding.fabText.isVisible = false
                }

                scrollX == scrollY -> {
                    binding.fabText.isVisible = true
                }

                else -> {
                    binding.fabText.isVisible = true
                }
            }
        }
    }

    private fun swipeToDelete(rvNotes: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val note = notesAdapter.currentList[position]
                var actionButtonTapped = false
                viewModel.delete(note)
                binding.search.apply {
                    hideKeyboard()
                    clearFocus()
                }
                if (binding.search.text.toString().isEmpty()) {
                    observeDataChanges()
                }
                val snackBar = Snackbar.make(
                    requireView(), "Note Deleted", Snackbar.LENGTH_LONG
                ).addCallback(object :
                    BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                        transientBottomBar?.setAction("Undo"){
                            viewModel.create(note)
                            actionButtonTapped=true
                            binding.noData.isVisible=false
                        }
                    }
                }).apply {
                    animationMode=Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.addNoteFab)
                }
                snackBar.setActionTextColor(ContextCompat.getColor(requireContext(),R.color.yellowOrange))
                snackBar.show()
            }
        }
        val itemTouchHelper=ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(rvNotes)
    }

    private fun observeDataChanges() {
        viewModel.read().observe(viewLifecycleOwner) { list ->
            binding.noData.isVisible = list.isEmpty()
            notesAdapter.submitList(list)
        }
    }

    private fun recyclerViewDisplay() {
        /*In this function first of all we will check it if phone is in portrait mode then
          we will show two notes in a row and if it is in landscape mode then we will show
          three notes in the row.*/
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> setUpRecyclerView(2)
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecyclerView(3)
        }
    }

    private fun setUpRecyclerView(spanCount: Int) {
        binding.rvNotes.apply {
            layoutManager =
                StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)

            notesAdapter = NotesAdapter()
            notesAdapter.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = notesAdapter

            postponeEnterTransition(300L, TimeUnit.MICROSECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        observeDataChanges()
    }
}
