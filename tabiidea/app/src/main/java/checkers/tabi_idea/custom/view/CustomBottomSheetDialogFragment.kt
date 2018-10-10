package checkers.tabi_idea.custom.view

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import checkers.tabi_idea.R

class CustomBottomSheetDialogFragment : BottomSheetDialogFragment() {

    var focusViewId: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        focusViewId = arguments?.getInt("viewId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun setupDialog(dialog: Dialog?, style: Int) {
        dialog?.apply {
            setContentView(R.layout.bottom_sheet)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(viewId: Int) =
                CustomBottomSheetDialogFragment()
                        .apply {
                            arguments = Bundle().apply {
                                putInt("viewId", viewId)
                            }
                        }
    }
}
