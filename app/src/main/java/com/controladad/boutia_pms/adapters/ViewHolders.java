package com.controladad.boutia_pms.adapters;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.databinding.CardItemBugsBinding;
import com.controladad.boutia_pms.databinding.CardItemCheckBoxFormElementBinding;
import com.controladad.boutia_pms.databinding.CardItemCustomEditTextFormElementsBinding;
import com.controladad.boutia_pms.databinding.CardItemCustomTextViewFormElementBinding;
import com.controladad.boutia_pms.databinding.CardItemDecimalNumberEditTextBinding;
import com.controladad.boutia_pms.databinding.CardItemDoubleButtonFormElementBinding;
import com.controladad.boutia_pms.databinding.CardItemDoubleCheckBoxFormElementBinding;
import com.controladad.boutia_pms.databinding.CardItemFiveSegmentedControlFormElementBinding;
import com.controladad.boutia_pms.databinding.CardItemFourSegmentedControlFormElementBinding;
import com.controladad.boutia_pms.databinding.CardItemGoodBadFormElementBinding;
import com.controladad.boutia_pms.databinding.CardItemImagesRecyclerViewBinding;
import com.controladad.boutia_pms.databinding.CardItemNumberEditViewFormElementsBinding;
import com.controladad.boutia_pms.databinding.CardItemSimpleTextViewFormElementsBinding;
import com.controladad.boutia_pms.databinding.CardItemSingleButtonFormElementBinding;
import com.controladad.boutia_pms.databinding.CardItemSixRadioButtonFormElementsBinding;
import com.controladad.boutia_pms.databinding.CardItemThreeSegmentControlFormElementBinding;
import com.controladad.boutia_pms.databinding.CardItemTwoSegmentFormElementsBinding;
import com.controladad.boutia_pms.databinding.CardItemUpdateDakalTamiratBinding;
import com.controladad.boutia_pms.view_models.items_view_models.BugsDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.CheckBoxDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.CustomEditTextDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.CustomTextViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.DoubleButtonDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.DoubleCheckBoxDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.GoodBadDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.ImagesRecyclerViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.NumberEditViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.SegmentedControlDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.SimpleTextViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.SingleButtonDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.SixRadioButtonDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.UpdateDakalTamiratDataModel;

import java.util.Objects;

public class ViewHolders {
    public static class ReadOnlyTypeViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemCustomTextViewFormElementBinding binding;
        public ReadOnlyTypeViewHolder(CardItemCustomTextViewFormElementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setCustomTextViewDataModel((CustomTextViewDataModel) dataModel);
            binding.executePendingBindings();
        }
    }
    public static class ThreeSegmentedTypeViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemThreeSegmentControlFormElementBinding binding;
        public ThreeSegmentedTypeViewHolder(CardItemThreeSegmentControlFormElementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            SegmentedControlDataModel segmentedControlDataModel = (SegmentedControlDataModel) dataModel;
            binding.setSegmentedControlDataModel(segmentedControlDataModel);
            binding.fourSegmentedGroup.clearCheck();
            binding.firstBox.setChecked(segmentedControlDataModel.isFirstSegmentChecked());
            binding.secondBox.setChecked(segmentedControlDataModel.isSecondSegmentChecked());
            binding.thirdBox.setChecked(segmentedControlDataModel.isThirdSegmentChecked());
            binding.executePendingBindings();
        }
    }
    public static class TwoSegmentedTypeViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemTwoSegmentFormElementsBinding binding;
        public TwoSegmentedTypeViewHolder(CardItemTwoSegmentFormElementsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            SegmentedControlDataModel segmentedControlDataModel = (SegmentedControlDataModel) dataModel;
            binding.setSegmentedControlDataModel(segmentedControlDataModel);
            binding.fourSegmentedGroup.clearCheck();
            binding.firstBox.setChecked(segmentedControlDataModel.isFirstSegmentChecked());
            binding.secondBox.setChecked(segmentedControlDataModel.isSecondSegmentChecked());
            binding.executePendingBindings();
        }
    }
    public static class EditTextTypeViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemCustomEditTextFormElementsBinding binding;
        public EditTextTypeViewHolder(CardItemCustomEditTextFormElementsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setCustomEditTextDataModel((CustomEditTextDataModel) dataModel);
            binding.executePendingBindings();
        }
    }
    public static class CheckBoxTypeViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemCheckBoxFormElementBinding binding;
        public CheckBoxTypeViewHolder(CardItemCheckBoxFormElementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            CheckBoxDataModel checkBoxDataModel = (CheckBoxDataModel) dataModel;
            binding.setCheckBoxDataModel(checkBoxDataModel);
            checkBoxDataModel.setCheckBox(binding.scb);
            binding.executePendingBindings();
        }
    }
    public static class FiveSegmentedTypeViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemFiveSegmentedControlFormElementBinding binding;
        public FiveSegmentedTypeViewHolder(CardItemFiveSegmentedControlFormElementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            SegmentedControlDataModel segmentedControlDataModel = (SegmentedControlDataModel) dataModel;
            binding.setSegmentedControlDataModel(segmentedControlDataModel);
            binding.fiveSegmentedGroup.clearCheck();
            binding.firstBox.setChecked(segmentedControlDataModel.isFirstSegmentChecked());
            binding.secondBox.setChecked(segmentedControlDataModel.isSecondSegmentChecked());
            binding.thirdBox.setChecked(segmentedControlDataModel.isThirdSegmentChecked());
            binding.fourthBox.setChecked(segmentedControlDataModel.isFourthSegmentChecked());
            binding.fifthBox.setChecked(segmentedControlDataModel.isFifthSegmentChecked());
            binding.executePendingBindings();
        }
    }

    public static class FourSegmentedTypeViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemFourSegmentedControlFormElementBinding binding;
        public FourSegmentedTypeViewHolder(CardItemFourSegmentedControlFormElementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            SegmentedControlDataModel segmentedControlDataModel = (SegmentedControlDataModel) dataModel;
            binding.setSegmentedControlDataModel(segmentedControlDataModel);
            binding.fourSegmentedGroup.clearCheck();
            binding.firstBox.setChecked(segmentedControlDataModel.isFirstSegmentChecked());
            binding.secondBox.setChecked(segmentedControlDataModel.isSecondSegmentChecked());
            binding.thirdBox.setChecked(segmentedControlDataModel.isThirdSegmentChecked());
            binding.fourthBox.setChecked(segmentedControlDataModel.isFourthSegmentChecked());
            binding.executePendingBindings();
        }
    }

    public static class SingleButtonViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemSingleButtonFormElementBinding binding;
        public SingleButtonViewHolder(CardItemSingleButtonFormElementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setSingleButtonDataModel((SingleButtonDataModel) dataModel);
            binding.executePendingBindings();
        }
    }

    public static class GoodBadViewHolder extends GeneralAdapter.GeneralViewHolder{
        private final CardItemGoodBadFormElementBinding binding;
        public GoodBadViewHolder(CardItemGoodBadFormElementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            GoodBadDataModel goodBadDataModel = (GoodBadDataModel) dataModel;
            binding.setGoodBadDataModel(goodBadDataModel);
            binding.radioGroupGoodBad.clearCheck();
            binding.radioBad.setChecked(goodBadDataModel.isBad());
            binding.radioGood.setChecked(goodBadDataModel.isGood());
            binding.executePendingBindings();
        }
    }
    public static class BugsViewHolder extends GeneralAdapter.GeneralViewHolder{

        private final CardItemBugsBinding binding;
        public BugsViewHolder(CardItemBugsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setBugsDataModel((BugsDataModel) dataModel);
            binding.executePendingBindings();
        }
    }
    public static class DoubleButtonHolder extends GeneralAdapter.GeneralViewHolder{

        private final CardItemDoubleButtonFormElementBinding binding;
        public DoubleButtonHolder(CardItemDoubleButtonFormElementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setDoubleButtonDataModel((DoubleButtonDataModel) dataModel);
            binding.executePendingBindings();
        }
    }
    public static class NumericalEditTextHolder extends GeneralAdapter.GeneralViewHolder{

        private final CardItemNumberEditViewFormElementsBinding binding;

        public NumericalEditTextHolder(CardItemNumberEditViewFormElementsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setNumberEditViewDataModel((NumberEditViewDataModel) dataModel);
            binding.executePendingBindings();
        }
    }

    public static class NumericalDecimalEditTextHolder extends GeneralAdapter.GeneralViewHolder{

        private final CardItemDecimalNumberEditTextBinding binding;

        public NumericalDecimalEditTextHolder(CardItemDecimalNumberEditTextBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setNumberEditViewDataModel((NumberEditViewDataModel) dataModel);
            binding.executePendingBindings();
        }
    }

    public static class SimpleTextViewHolder extends GeneralAdapter.GeneralViewHolder{

        private final CardItemSimpleTextViewFormElementsBinding binding;

        public SimpleTextViewHolder(CardItemSimpleTextViewFormElementsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setSimpleTextViewDataModel((SimpleTextViewDataModel) dataModel);
            binding.executePendingBindings();
        }
    }

    public static class ImageRecyclerViewHolder extends GeneralAdapter.GeneralViewHolder {

        private final CardItemImagesRecyclerViewBinding binding;

        public ImageRecyclerViewHolder(CardItemImagesRecyclerViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setImagesRecyclerViewDataModel((ImagesRecyclerViewDataModel) dataModel);
            binding.executePendingBindings();
        }
    }

    public static class DoubleCheckBoxViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemDoubleCheckBoxFormElementBinding binding;
        public DoubleCheckBoxViewHolder(CardItemDoubleCheckBoxFormElementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            DoubleCheckBoxDataModel doubleCheckBoxDataModel = (DoubleCheckBoxDataModel) dataModel;
            binding.setDoubleCheckBoxDataModel(doubleCheckBoxDataModel);
            doubleCheckBoxDataModel.setLeftCheckBox(binding.leftCheckBoc);
            doubleCheckBoxDataModel.setRightCheckBox(binding.rightCheckBox);
            doubleCheckBoxDataModel.setLeftLinearLayout(binding.leftLinearLayout);
            binding.executePendingBindings();
        }
    }

    public static class SixRadioButtonViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemSixRadioButtonFormElementsBinding binding;
        public SixRadioButtonViewHolder(CardItemSixRadioButtonFormElementsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            SixRadioButtonDataModel sixRadioButtonDataModel = (SixRadioButtonDataModel) dataModel;
            binding.setSixRadioButtonDataModel(sixRadioButtonDataModel);
            sixRadioButtonDataModel.setLeftRadioGroup(binding.leftRadioGroup);
            sixRadioButtonDataModel.setRightRadioGroup(binding.rightRadioGroup);
            binding.leftRadioGroup.clearCheck();
            binding.rightRadioGroup.clearCheck();;
            if(Objects.equals(sixRadioButtonDataModel.getSelectedItem(), sixRadioButtonDataModel.getFirstRadioButton()))
                 binding.leftRadioGroup.check(R.id.firstRadioButton);
            else if(Objects.equals(sixRadioButtonDataModel.getSelectedItem(), sixRadioButtonDataModel.getSecondRadioButton()))
                binding.leftRadioGroup.check(R.id.secondRadioButton);
            else if(Objects.equals(sixRadioButtonDataModel.getSelectedItem(), sixRadioButtonDataModel.getThirdRadioButton()))
                binding.leftRadioGroup.check(R.id.thirdRadioButton);
            else if(Objects.equals(sixRadioButtonDataModel.getSelectedItem(), sixRadioButtonDataModel.getFourthRadioButton()))
                binding.rightRadioGroup.check(R.id.fourthRadioButton);
            else if(Objects.equals(sixRadioButtonDataModel.getSelectedItem(), sixRadioButtonDataModel.getFifthRadioButton()))
                binding.rightRadioGroup.check(R.id.fifthRadioButton);
            else if(Objects.equals(sixRadioButtonDataModel.getSelectedItem(), sixRadioButtonDataModel.getSixthRadioButton()))
                binding.rightRadioGroup.check(R.id.sixthRadioButton);
            binding.executePendingBindings();
        }
    }

    public static class UpdateDakalTamiratViewHolder extends GeneralAdapter.GeneralViewHolder {
        private final CardItemUpdateDakalTamiratBinding binding;
        public UpdateDakalTamiratViewHolder(CardItemUpdateDakalTamiratBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(GeneralDataModel dataModel) {
            binding.setViewModel((UpdateDakalTamiratDataModel) dataModel);
            binding.executePendingBindings();
        }
    }

}
