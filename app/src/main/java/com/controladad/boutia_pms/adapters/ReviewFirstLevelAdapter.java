package com.controladad.boutia_pms.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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

import static com.controladad.boutia_pms.adapters.ViewHolders.BugsViewHolder;
import static com.controladad.boutia_pms.adapters.ViewHolders.CheckBoxTypeViewHolder;
import static com.controladad.boutia_pms.adapters.ViewHolders.DoubleButtonHolder;
import static com.controladad.boutia_pms.adapters.ViewHolders.EditTextTypeViewHolder;
import static com.controladad.boutia_pms.adapters.ViewHolders.FiveSegmentedTypeViewHolder;
import static com.controladad.boutia_pms.adapters.ViewHolders.FourSegmentedTypeViewHolder;
import static com.controladad.boutia_pms.adapters.ViewHolders.GoodBadViewHolder;
import static com.controladad.boutia_pms.adapters.ViewHolders.NumericalEditTextHolder;
import static com.controladad.boutia_pms.adapters.ViewHolders.SingleButtonViewHolder;
import static com.controladad.boutia_pms.adapters.ViewHolders.ThreeSegmentedTypeViewHolder;
import static com.controladad.boutia_pms.adapters.ViewHolders.TwoSegmentedTypeViewHolder;
import static com.controladad.boutia_pms.utility.Constants.BUGS_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.CHECK_BOX_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.CUSTOM_EDIT_TEXT_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.CUSTOM_TEXT_VIEW_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.DOUBLE_BUTTON_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.DOUBLE_CHECK_BOX_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.FIVE_SEGMENT_CONTROL_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.FOUR_SEGMENT_CONTROL_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.GOOD_BAD_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.IMAGES_RECYCLER_VIEW_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.NUMBER_DECIMAL_EDIT_VIEW_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.NUMBER_EDIT_VIEW_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.SIMPLE_TEXT_VIEW_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.SINGLE_BUTTON_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.SIX_RADIO_BUTTON_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.THREE_SEGMENT_CONTROL_CARD_ITEM_KEY;
import static com.controladad.boutia_pms.utility.Constants.TWO_SEGMENT_CARD_ITEM_KEY;

public class ReviewFirstLevelAdapter extends GeneralAdapter {

    public ReviewFirstLevelAdapter() {
        super();
    }


    @Override
    public void itemResizing(RecyclerView.ViewHolder holder) {}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case  CUSTOM_TEXT_VIEW_CARD_ITEM_KEY:{
                CardItemCustomTextViewFormElementBinding binding = CardItemCustomTextViewFormElementBinding.inflate(layoutInflater,parent,false);
                return new ViewHolders.ReadOnlyTypeViewHolder(binding);
            }
            case THREE_SEGMENT_CONTROL_CARD_ITEM_KEY:{
                CardItemThreeSegmentControlFormElementBinding binding = CardItemThreeSegmentControlFormElementBinding.inflate(layoutInflater,parent,false);
                return new ThreeSegmentedTypeViewHolder(binding);
            }
            case TWO_SEGMENT_CARD_ITEM_KEY:{
                CardItemTwoSegmentFormElementsBinding binding = CardItemTwoSegmentFormElementsBinding.inflate(layoutInflater,parent,false);
                return new TwoSegmentedTypeViewHolder(binding);
            }
            case CUSTOM_EDIT_TEXT_CARD_ITEM_KEY:{
                CardItemCustomEditTextFormElementsBinding binding = CardItemCustomEditTextFormElementsBinding.inflate(layoutInflater,parent,false);
                return new EditTextTypeViewHolder(binding);
            }
            case CHECK_BOX_CARD_ITEM_KEY:{
                CardItemCheckBoxFormElementBinding binding = CardItemCheckBoxFormElementBinding.inflate(layoutInflater,parent,false);
                return new CheckBoxTypeViewHolder(binding);
            }
            case FIVE_SEGMENT_CONTROL_CARD_ITEM_KEY:{
                CardItemFiveSegmentedControlFormElementBinding binding = CardItemFiveSegmentedControlFormElementBinding.inflate(layoutInflater,parent,false);
                return new FiveSegmentedTypeViewHolder(binding);
            }
            case GOOD_BAD_CARD_ITEM_KEY:{
                CardItemGoodBadFormElementBinding binding = CardItemGoodBadFormElementBinding.inflate(layoutInflater,parent,false);
                return new GoodBadViewHolder(binding);
            }
            case FOUR_SEGMENT_CONTROL_CARD_ITEM_KEY:{
                CardItemFourSegmentedControlFormElementBinding binding = CardItemFourSegmentedControlFormElementBinding.inflate(layoutInflater,parent,false);
                return new FourSegmentedTypeViewHolder(binding);
            }
            case SINGLE_BUTTON_CARD_ITEM_KEY:{
                CardItemSingleButtonFormElementBinding binding = CardItemSingleButtonFormElementBinding.inflate(layoutInflater,parent,false);
                return new SingleButtonViewHolder(binding);
            }
            case BUGS_ITEM_KEY:{
                CardItemBugsBinding binding = CardItemBugsBinding.inflate(layoutInflater,parent,false);
                return new BugsViewHolder(binding);
            }
            case DOUBLE_BUTTON_CARD_ITEM_KEY:{
                CardItemDoubleButtonFormElementBinding binding = CardItemDoubleButtonFormElementBinding.inflate(layoutInflater,parent,false);
                return new DoubleButtonHolder(binding);
            }
            case NUMBER_EDIT_VIEW_CARD_ITEM_KEY:{
                CardItemNumberEditViewFormElementsBinding binding = CardItemNumberEditViewFormElementsBinding.inflate(layoutInflater,parent,false);
                return new NumericalEditTextHolder(binding);
            }
            case NUMBER_DECIMAL_EDIT_VIEW_CARD_ITEM_KEY:{
                CardItemDecimalNumberEditTextBinding binding = CardItemDecimalNumberEditTextBinding.inflate(layoutInflater,parent,false);
                return new ViewHolders.NumericalDecimalEditTextHolder(binding);
            }
            case SIMPLE_TEXT_VIEW_ITEM_KEY:{
                CardItemSimpleTextViewFormElementsBinding binding = CardItemSimpleTextViewFormElementsBinding.inflate(layoutInflater,parent,false);
                return new ViewHolders.SimpleTextViewHolder(binding);
            }
            case IMAGES_RECYCLER_VIEW_ITEM_KEY:{
                CardItemImagesRecyclerViewBinding binding = CardItemImagesRecyclerViewBinding.inflate(layoutInflater,parent,false);
                return new ViewHolders.ImageRecyclerViewHolder(binding);
            }
            case DOUBLE_CHECK_BOX_CARD_ITEM_KEY:{
                CardItemDoubleCheckBoxFormElementBinding binding = CardItemDoubleCheckBoxFormElementBinding.inflate(layoutInflater,parent,false);
                return new ViewHolders.DoubleCheckBoxViewHolder(binding);
            }
            case SIX_RADIO_BUTTON_CARD_ITEM_KEY:{
                CardItemSixRadioButtonFormElementsBinding binding = CardItemSixRadioButtonFormElementsBinding.inflate(layoutInflater,parent,false);
                return new ViewHolders.SixRadioButtonViewHolder(binding);
            }

        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItemsModelList().get(position)!=null)
            return getItemsModelList().get(position).getKey();
        return 1;
    }


}
