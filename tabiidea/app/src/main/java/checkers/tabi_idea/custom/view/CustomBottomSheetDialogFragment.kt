package checkers.tabi_idea.custom.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import checkers.tabi_idea.R
import kotlinx.android.synthetic.main.bottom_sheet.*

class CustomBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var focusViewId: Int = 0
    private var listener: Listener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        focusViewId = arguments!!.getInt("viewId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet, container, false)
//        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addButton.setOnClickListener {
            listener?.onAddClicked(focusViewId)
        }
        deleteButton.setOnClickListener {
            listener?.onDeleteClicked(focusViewId)
        }
        editButton.setOnClickListener {
            listener?.onEditClicked(focusViewId)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment

        listener =
                if (parent != null) {
                    parent as CustomBottomSheetDialogFragment.Listener
                } else {
                    context as CustomBottomSheetDialogFragment.Listener
                }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun setupDialog(dialog: Dialog?, style: Int) {
        dialog?.apply {
            setContentView(R.layout.bottom_sheet)
        }
    }

    interface Listener {
        fun onAddClicked(position: Int)
        fun onDeleteClicked(position: Int)
        fun onEditClicked(position: Int)
    }

    companion object {
        @JvmStatic
        fun newInstance(viewId: Int): CustomBottomSheetDialogFragment {
            return CustomBottomSheetDialogFragment()
                    .apply {
                        arguments = Bundle().apply {
                            putInt("viewId", viewId)
                        }
                    }
        }
    }
}