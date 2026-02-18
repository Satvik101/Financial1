package com.fincalc.app.feature.calculator

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fincalc.app.databinding.BottomSheetInfoBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CalculatorInfoBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvInfoTitle.text = requireArguments().getString(ARG_TITLE).orEmpty()
        binding.tvInfoBody.text = requireArguments().getString(ARG_BODY).orEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_BODY = "body"

        fun newInstance(title: String, body: String): CalculatorInfoBottomSheet {
            return CalculatorInfoBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_BODY, body)
                }
            }
        }
    }
}
