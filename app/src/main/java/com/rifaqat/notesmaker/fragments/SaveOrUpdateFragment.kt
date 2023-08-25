package com.rifaqat.notesmaker.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import com.rifaqat.notesmaker.MainActivity
import com.rifaqat.notesmaker.R
import com.rifaqat.notesmaker.databinding.BottomSheetLayoutBinding
import com.rifaqat.notesmaker.databinding.FragmentSaveOrUpdateBinding
import com.rifaqat.notesmaker.models.NotesEntity
import com.rifaqat.notesmaker.utils.hideKeyboard
import com.rifaqat.notesmaker.viewmodels.NotesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class SaveOrUpdateFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSaveOrUpdateBinding
    private val viewModel: NotesViewModel by activityViewModels()
    private var note: NotesEntity? = null
    private var color = -1
    private val currentDate = SimpleDateFormat.getInstance().format(Date())
    private val job = CoroutineScope(Dispatchers.Main)
    private val args: SaveOrUpdateFragmentArgs by navArgs()
    private lateinit var result: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*to use a MaterialContainerTransform animation in the onCreate method of an Activity or
        Fragment. The MaterialContainerTransform is a built-in transition that is
        part of the Material Design Components library and can be used to create smooth and
        visually appealing transitions between UI elements.*/
        val animation = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration = 300L
        }
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment.data binding initialization
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_or_update, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Accessing MainActivity
        val activity = activity as MainActivity

        note = args.note

        //Find nav controller.
        navController = Navigation.findNavController(view)

        ViewCompat.setTransitionName(binding.noteContentFragmentParent,"recyclerView_${args.note?.key}")

//        Here we are going to set click on back button and hide keyboard as well as go to previous fragment
        binding.backBtn.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }

//        Set click on save note icon to save the current note to database.
        binding.saveNote.setOnClickListener {
            saveNote()
        }

        //        Focus block
        try {
            binding.etDescription.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.bottomBar.visibility = View.VISIBLE
                    binding.etDescription.setStylesBar(binding.styleBar)
                } else {
                    binding.bottomBar.visibility = View.GONE
                }
            }
        } catch (e: Throwable) {
            Log.d("TAG", e.stackTraceToString())
        }

        //        Now we are going to set click on pick color bottom to set default color on the fragment/Specific note.
        binding.fabColorPick.setOnClickListener {
            val bottomSheetDialog =
                BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)

            val bottomSheetView: View =
                layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
            with(bottomSheetDialog) {
                setContentView(bottomSheetView)
                show()
            }

            val bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView)

            bottomSheetBinding.apply {
                colorPicker.apply {
                    setSelectedColor(color)

                    setOnColorSelectedListener { value ->
                        color = value

                        binding.apply {
                            noteContentFragmentParent.setBackgroundColor(color)
                            bottomBar.setBackgroundColor(color)
                            toolbarFragmentNoteContent.setBackgroundColor(color)
                            activity.window.statusBarColor = color
                        }
                        bottomSheetBinding.bottomSheet.setCardBackgroundColor(color)
                    }
                }
                bottomSheet.setCardBackgroundColor(color)
            }
            bottomSheetView.post {
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        //open with existing note items.
        setUpNote()
    }

    private fun setUpNote() {
        val note = args.note
        val title = binding.etTitle
        val description = binding.etDescription
        val lastEdited = binding.lastEdited

        if (note == null) {
            binding.lastEdited.text =
                getString(R.string.edited_on, SimpleDateFormat.getDateTimeInstance().format(Date()))
        } else {
            title.setText(note.title)
            description.renderMD(note.description)
            lastEdited.text=getString(R.string.edited_on,note.date)
            color=note.colorCode
            binding.apply {
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(color)
                }
                toolbarFragmentNoteContent.setBackgroundColor(color)
                bottomBar.setBackgroundColor(color)
            }
            activity?.window?.statusBarColor=note.colorCode
        }
    }


    //    It is the function to save Note call by saveNote btn click in onViewCreated()
    private fun saveNote() {
        if (binding.etTitle.text.toString().isEmpty() || binding.etDescription.text.toString()
                .isEmpty()
        ) {
            Toast.makeText(activity, "Title or Description is Empty.", Toast.LENGTH_SHORT).show()
        } else {
            note = args.note

            when (note) { // Safe call since the argument is nullable
                null -> {
                    // If null means the note is creating first time, create a new note
                    viewModel.create(
                        NotesEntity(
                            0,
                            binding.etTitle.text.toString(),
                            binding.etDescription.getMD(),
                            currentDate,
                            color
                        )
                    )

                    result = "Note Saved"
                    setFragmentResult(
                        "key", bundleOf("bundleKey" to result)
                    )

                    navController.navigate(SaveOrUpdateFragmentDirections.actionSaveOrUpdateFragment2ToNoteFragment22())
                }

                else -> {
                    //update note
                    updateNote()
                    navController.popBackStack()
                }
            }
        }
    }

    private fun updateNote() {
        if (note != null) {
            viewModel.update(
                NotesEntity(
                    note!!.key,
                    binding.etTitle.text.toString(),
                    binding.etDescription.getMD(),
                    currentDate,
                    color
                )
            )
        }
    }
}