package com.controladad.boutia_pms.view_models;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Parcel;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.controladad.boutia_pms.R;
import com.controladad.boutia_pms.adapters.ReviewFirstLevelAdapter;
import com.controladad.boutia_pms.adapters.StatefulRecyclerView;
import com.controladad.boutia_pms.databinding.FragmentReviewFourthLevelBinding;
import com.controladad.boutia_pms.location.LocationService;
import com.controladad.boutia_pms.models.data_models.ProjectModel;
import com.controladad.boutia_pms.utility.Constants;
import com.controladad.boutia_pms.view_models.items_view_models.CheckBoxDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.DoubleCheckBoxDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.GeneralDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.GoodBadDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.ImagesRecyclerViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.NumberEditViewDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.SegmentedControlDataModel;
import com.controladad.boutia_pms.view_models.items_view_models.SingleButtonDataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lombok.Getter;

public class ReviewFourthLevelVM extends GeneralVM {

    @Getter
    private ReviewFirstLevelAdapter adapter = new ReviewFirstLevelAdapter();
    private StatefulRecyclerView recyclerView;
    private ProjectModel.ReviewModel reviewModel;
    private ProjectModel.ReviewModel reviewModelHelper;
    private List<GeneralDataModel> itemModelList = new ArrayList<>();
    private SegmentedControlDataModel simeMohafezVaMolhaght;
    private SegmentedControlDataModel opgwTakSheld;
    private SegmentedControlDataModel jointBox;
    private SegmentedControlDataModel aJointBox;
    private SegmentedControlDataModel bJointBox;
    private List<GeneralDataModel> takShieldAFieldList = new ArrayList<>();
    private GoodBadDataModel takShieldA;
    private SegmentedControlDataModel opgwDoShieldA;
    private List<GeneralDataModel> doShieldAFieldList = new ArrayList<>();
    private GoodBadDataModel doShieldA;
    private SegmentedControlDataModel opgwDoShieldB;
    private List<GeneralDataModel> doShieldBFieldList = new ArrayList<>();
    private GoodBadDataModel doShieldB;
    private List<GeneralDataModel> laneParandeFieldsList = new ArrayList<>();
    private GoodBadDataModel laneParande;

    private List<GeneralDataModel> ashiaEzafeFieldsList = new ArrayList<>();
    private GoodBadDataModel ashiaEzafe;

    @Override
    public void onCreateFragment() {
        super.onCreateFragment();
        fieldsFilling();
        ashiaEzafe.setBadButtonText(context.getString(R.string.exist));
        ashiaEzafe.setGoodButtonText(context.getString(R.string.dose_not_exist));
        itemModelList.clear();
        itemModelList.add(simeMohafezVaMolhaght);
        if (Objects.equals(reviewModel.getSimeMohafezVaMolhaghat().getType(), context.getString(R.string.tak_shield))) {
            itemModelList.add(opgwTakSheld);
            if (Objects.equals(reviewModel.getSimeMohafezVaMolhaghat().getTakShieldFields().getJenseSim(), context.getString(R.string.opgw)))
                itemModelList.add(jointBox);
            itemModelList.add(takShieldA);
        }
        if (Objects.equals(reviewModel.getSimeMohafezVaMolhaghat().getType(), context.getString(R.string.do_shield))) {
            itemModelList.add(opgwDoShieldA);
            if (Objects.equals(reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().getJenseSimA(), context.getString(R.string.opgw)))
                itemModelList.add(aJointBox);
            itemModelList.add(doShieldA);
            itemModelList.add(opgwDoShieldB);
            if (Objects.equals(reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().getJenseSimB(), context.getString(R.string.opgw)))
                itemModelList.add(bJointBox);
            itemModelList.add(doShieldB);
        }
        itemModelList.add(laneParande);
        itemModelList.add(ashiaEzafe);
        simeMohafezVaMolhaght.setOnCheckedChangeListener(onSimeMohafezCheckChanged());
        itemModelList.add(new ImagesRecyclerViewDataModel());
        itemModelList.add(new SingleButtonDataModel(context.getString(R.string.ok_and_continue), onScanDialogOkButtonClicked(), () -> "", (s) -> {
        }));
        adapter.updateData(itemModelList);
    }

    private View.OnClickListener onScanDialogOkButtonClicked() {
        return v -> {
            for (GeneralDataModel dataModel : itemModelList) {
                if (!dataModel.isItemFilled()) {
                    showSnackBar("لطفا فیلد " + dataModel.getItemName() + " را پر کنید.");
                    if (recyclerView.getLayoutManager() != null)
                        recyclerView.getLayoutManager().scrollToPosition(adapter.getItemsModelList().indexOf(dataModel));
                    return;
                }
            }
            if (Objects.equals(reviewModel.getSimeMohafezVaMolhaghat().getType(), context.getString(R.string.tak_shield))) {
                if (!checkForFillingDataModel(opgwTakSheld))
                    return;
                if (Objects.equals(reviewModel.getSimeMohafezVaMolhaghat().getTakShieldFields().getJenseSim(), context.getString(R.string.opgw)))
                    if (!checkForFillingDataModel(jointBox))
                        return;
                if (!checkForFillingDataModel(takShieldA))
                    return;
            }
            if (Objects.equals(reviewModel.getSimeMohafezVaMolhaghat().getType(), context.getString(R.string.do_shield))) {
                if (!checkForFillingDataModel(opgwDoShieldA))
                    return;
                if (Objects.equals(reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().getJenseSimA(), context.getString(R.string.opgw)))
                    if (!checkForFillingDataModel(aJointBox))
                        return;
                if (!checkForFillingDataModel(doShieldA))
                    return;
                if (!checkForFillingDataModel(opgwDoShieldB))
                    return;
                if (Objects.equals(reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().getJenseSimB(), context.getString(R.string.opgw)))
                    if (!checkForFillingDataModel(bJointBox))
                        return;
                if (!checkForFillingDataModel(doShieldB))
                    return;
            }

            getRouter().navigateTo(Constants.REVIEW_FIFTH_LEVEL_SCREEN_KEY, new ReviewFifthLevelVM());

        };
    }

    private boolean checkForFillingDataModel(GeneralDataModel dataModel){
        if (!dataModel.isItemFilled()) {
            showSnackBar("لطفا فیلد " + dataModel.getItemName() + " را پر کنید.");
            if (recyclerView.getLayoutManager() != null)
                recyclerView.getLayoutManager().scrollToPosition(adapter.getItemsModelList().indexOf(dataModel));
            return false;
        }
        return true;
    }

    private CompoundButton.OnCheckedChangeListener onSimeMohafezCheckChanged() {
        return (buttonView, isChecked) -> {
            if (isChecked) {
                String buttonText = (String) buttonView.getText();
                if (Objects.equals(buttonText, context.getString(R.string.tak_shield))) {
                    removeDoshieldItems();
                    addTakShieldItems();
                } else if (Objects.equals(buttonText, context.getString(R.string.do_shield))) {
                    removeTakShieldItems();
                    addDoshieldItems();
                } else {
                    removeTakShieldItems();
                    removeDoshieldItems();
                }
            }
        };
    }

    private void addTakShieldItems() {
        if (!adapter.getItemsModelList().contains(opgwTakSheld)) {
            int position = adapter.getItemsModelList().indexOf(simeMohafezVaMolhaght) + 1;
            adapter.getItemsModelList().add(position, opgwTakSheld);
            adapter.getItemsModelList().add(position + 1, takShieldA);
            int addedItems = 2;
            if (Objects.equals(opgwTakSheld.getSelectedItemText(), context.getString(R.string.opgw))) {
                adapter.getItemsModelList().add(position + 1, jointBox);
                addedItems = addedItems + 1;
            }
            try {
                adapter.notifyItemRangeInserted(position, addedItems);
            } catch (IllegalStateException e) {
                adapter.setHasItemsChanged(true);
            }
        }
    }

    private void addDoshieldItems() {
        if (!adapter.getItemsModelList().contains(opgwDoShieldA)) {
            int position = adapter.getItemsModelList().indexOf(simeMohafezVaMolhaght);
            adapter.getItemsModelList().add(position + 1, opgwDoShieldA);
            adapter.getItemsModelList().add(position + 2, doShieldA);
            int addedItems = 4;
            if (Objects.equals(opgwDoShieldA.getSelectedItemText(), context.getString(R.string.opgw))) {
                adapter.getItemsModelList().add(position + 2, aJointBox);
                addedItems = addedItems + 1;
                position = position + 1;
            }
            adapter.getItemsModelList().add(position + 3, opgwDoShieldB);
            adapter.getItemsModelList().add(position + 4, doShieldB);
            if (Objects.equals(opgwDoShieldB.getSelectedItemText(), context.getString(R.string.opgw))) {
                adapter.getItemsModelList().add(position + 4, bJointBox);
                addedItems = addedItems + 1;
            }
            try {
                adapter.notifyItemRangeInserted(position + 1, addedItems);
            } catch (IllegalStateException e) {
                adapter.setHasItemsChanged(true);
            }
        }
    }

    private void removeTakShieldItems() {
        if (adapter.getItemsModelList().contains(opgwTakSheld)) {
            int position = adapter.getItemsModelList().indexOf(opgwTakSheld);
            if (takShieldA.isExpanded()) takShieldA.onClickListener().onClick(null);
            adapter.getItemsModelList().remove(opgwTakSheld);
            adapter.getItemsModelList().remove(takShieldA);
            int removedItems = 2;
            if (adapter.getItemsModelList().contains(jointBox)) {
                adapter.getItemsModelList().remove(jointBox);
                removedItems = removedItems + 1;
            }
            try {
                adapter.notifyItemRangeRemoved(position, removedItems);
            } catch (IllegalStateException e) {
                adapter.setHasItemsChanged(true);
            }
        }
    }

    private void removeDoshieldItems() {
        if (adapter.getItemsModelList().contains(opgwDoShieldA)) {
            int position = adapter.getItemsModelList().indexOf(opgwDoShieldA);
            if (doShieldA.isExpanded()) doShieldA.onClickListener().onClick(null);
            if (doShieldB.isExpanded()) doShieldB.onClickListener().onClick(null);
            adapter.getItemsModelList().remove(opgwDoShieldA);
            adapter.getItemsModelList().remove(doShieldA);
            adapter.getItemsModelList().remove(opgwDoShieldB);
            adapter.getItemsModelList().remove(doShieldB);
            int removedItems = 4;
            if (adapter.getItemsModelList().contains(aJointBox)) {
                adapter.getItemsModelList().remove(aJointBox);
                removedItems = removedItems + 1;
            }
            if (adapter.getItemsModelList().contains(bJointBox)) {
                adapter.getItemsModelList().remove(bJointBox);
                removedItems = removedItems + 1;
            }
            try {
                adapter.notifyItemRangeRemoved(position, removedItems);
            } catch (IllegalStateException e) {
                adapter.setHasItemsChanged(true);
            }
        }
    }

    private void fieldsFilling() {
        simeMohafezFieldsFilling();
        laneParandeVaAshiaEzafeFieldsFilling();
    }

    private void simeMohafezFieldsFilling() {
        ProjectModel.TakShieldFields takShieldFields = reviewModelHelper.getSimeMohafezVaMolhaghat().getTakShieldFields();
        ProjectModel.DoShieldFields doShieldFields = reviewModelHelper.getSimeMohafezVaMolhaghat().getDoShieldFields();
        ProjectModel.SimeMohafezBadCondition takShieldBadCondition = takShieldFields.getSimeMohafezBadCondition();
        ProjectModel.SimeMohafezBadCondition aDoShieldBadCondition = doShieldFields.getSimeMohafezBadConditionA();
        ProjectModel.SimeMohafezBadCondition bDoShieldBadCondition = doShieldFields.getSimeMohafezBadConditionB();
        simeMohafezVaMolhaght = new SegmentedControlDataModel(context.getString(R.string.tak_shield), context.getString(R.string.do_shield), context.getString(R.string.faghed_shield), context.getString(R.string.sime_mohafez_va_molhaghat),
                () -> reviewModelHelper.getSimeMohafezVaMolhaghat().getType(),
                (s) -> {
                    reviewModelHelper.getSimeMohafezVaMolhaghat().setType(s);
                    reviewModel.getSimeMohafezVaMolhaghat().setType(s);
                    if (Objects.equals(s, context.getString(R.string.tak_shield))) {
                        ProjectModel.OpgwFields opgwFields = Objects.equals(takShieldFields.getJenseSim(), context.getString(R.string.opgw)) ? takShieldFields.getOpgwFields() : null;
                        ProjectModel.SimeMohafezBadCondition simeMohafezBadCondition = Objects.equals(takShieldFields.getCondition(), Constants.IS_BAD) ? takShieldFields.getSimeMohafezBadCondition() : null;
                        reviewModel.getSimeMohafezVaMolhaghat().setTakShieldFields(new ProjectModel.TakShieldFields(takShieldFields.getJenseSim(), opgwFields,
                                takShieldFields.getCondition(), simeMohafezBadCondition));
                        reviewModel.getSimeMohafezVaMolhaghat().setDoShieldFields(null);
                    } else if (Objects.equals(s, context.getString(R.string.do_shield))) {
                        ProjectModel.OpgwFields aOpgwFields = Objects.equals(doShieldFields.getJenseSimA(), context.getString(R.string.opgw)) ? doShieldFields.getOpgwFieldsA() : null;
                        ProjectModel.SimeMohafezBadCondition aSimeMohafezBadCondition = Objects.equals(doShieldFields.getConditionA(), Constants.IS_BAD) ? doShieldFields.getSimeMohafezBadConditionA() : null;
                        ProjectModel.OpgwFields bOpgwFields = Objects.equals(doShieldFields.getJenseSimB(), context.getString(R.string.opgw)) ? doShieldFields.getOpgwFieldsB() : null;
                        ProjectModel.SimeMohafezBadCondition bSimeMohafezBadCondition = Objects.equals(doShieldFields.getConditionB(), Constants.IS_BAD) ? doShieldFields.getSimeMohafezBadConditionB() : null;
                        reviewModel.getSimeMohafezVaMolhaghat().setDoShieldFields(new ProjectModel.DoShieldFields(doShieldFields.getJenseSimA(), aOpgwFields,
                                doShieldFields.getConditionA(), aSimeMohafezBadCondition, doShieldFields.getJenseSimB(), bOpgwFields,
                                doShieldFields.getConditionB(), bSimeMohafezBadCondition));
                        reviewModel.getSimeMohafezVaMolhaghat().setTakShieldFields(null);
                    } else {
                        reviewModel.getSimeMohafezVaMolhaghat().setTakShieldFields(null);
                        reviewModel.getSimeMohafezVaMolhaghat().setDoShieldFields(null);
                    }
                });
        opgwTakSheld = new SegmentedControlDataModel(context.getString(R.string.shield_wire), context.getString(R.string.opgw), null,
                () -> takShieldFields.getJenseSim(),
                (s) -> {
                    takShieldFields.setJenseSim(s);
                    reviewModel.getSimeMohafezVaMolhaghat().getTakShieldFields().setJenseSim(s);
                    if (Objects.equals(s, context.getString(R.string.opgw))) {
                        reviewModel.getSimeMohafezVaMolhaghat().getTakShieldFields().setOpgwFields(takShieldFields.getOpgwFields());
                    } else if (Objects.equals(s, context.getString(R.string.shield_wire))) {
                        reviewModel.getSimeMohafezVaMolhaghat().getTakShieldFields().setOpgwFields(null);
                    }
                });
        jointBox = new SegmentedControlDataModel(context.getString(R.string.dose_not_have), context.getString(R.string.good), context.getString(R.string.bad), context.getString(R.string.joint_box_a),
                () -> takShieldFields.getOpgwFields().getJointBox(),
                (s) -> {
                    takShieldFields.getOpgwFields().setJointBox(s);
                });
        opgwTakSheld.setOnCheckedChangeListener(onJointBoxCheckChanged(opgwTakSheld, jointBox));
        takShieldA = new GoodBadDataModel("A", takShieldAFieldList, adapter,
                takShieldFields::getCondition, (s) -> {
            takShieldFields.setCondition(s);
            reviewModel.getSimeMohafezVaMolhaghat().getTakShieldFields().setCondition(s);
            switch (s) {
                case Constants.IS_BAD:
                    reviewModel.getSimeMohafezVaMolhaghat().getTakShieldFields().setSimeMohafezBadCondition(takShieldBadCondition);
                    break;
                case Constants.IS_GOOD:
                    reviewModel.getSimeMohafezVaMolhaghat().getTakShieldFields().setSimeMohafezBadCondition(null);
            }
        }, takShieldBadCondition::getDescription, takShieldBadCondition::setDescription);
        DoubleCheckBoxDataModel acharKeshiDamperTakShieldA = new DoubleCheckBoxDataModel(context.getString(R.string.achar_keshi), null, context.getString(R.string.damper), null,
                takShieldBadCondition::getAcharKeshi, takShieldBadCondition::setAcharKeshi, takShieldBadCondition::getDamper, takShieldBadCondition::setDamper);
        DoubleCheckBoxDataModel lolePeressKafiTakShieldA = new DoubleCheckBoxDataModel(context.getString(R.string.lole_press), null, context.getString(R.string.kafi), null,
                takShieldBadCondition::getLoolePress, takShieldBadCondition::setLoolePress, takShieldBadCondition::getKafi, takShieldBadCondition::setKafi);
        DoubleCheckBoxDataModel colompArmoradTakShieldA = new DoubleCheckBoxDataModel(context.getString(R.string.colomp), null, context.getString(R.string.armorad), null,
                takShieldBadCondition::getKolomp, takShieldBadCondition::setKolomp, takShieldBadCondition::getArmorad, takShieldBadCondition::setArmorad);
        DoubleCheckBoxDataModel jamperEshpilTakShieldA = new DoubleCheckBoxDataModel(context.getString(R.string.jumper), null, context.getString(R.string.eshpil), null,
                takShieldBadCondition::getJamper, takShieldBadCondition::setJamper, takShieldBadCondition::getEshpil, takShieldBadCondition::setEshpil);
        CheckBoxDataModel gooyEkhtar = new CheckBoxDataModel("گوی اخطار",null,takShieldBadCondition::getGoyeEkhtar,takShieldBadCondition::setGoyeEkhtar);
        GeneralDataModel[] takShieldAFields = {acharKeshiDamperTakShieldA, lolePeressKafiTakShieldA, colompArmoradTakShieldA, jamperEshpilTakShieldA, gooyEkhtar};
        opgwDoShieldA = new SegmentedControlDataModel(context.getString(R.string.shield_wire), context.getString(R.string.opgw), null,
                () -> doShieldFields.getJenseSimA(),
                (s) -> {
                    doShieldFields.setJenseSimA(s);
                    reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().setJenseSimA(s);
                    if (Objects.equals(s, context.getString(R.string.opgw))) {
                        reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().setOpgwFieldsA(doShieldFields.getOpgwFieldsA());
                    } else if (Objects.equals(s, context.getString(R.string.shield_wire))) {
                        reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().setOpgwFieldsA(null);
                    }
                });
        aJointBox = new SegmentedControlDataModel(context.getString(R.string.dose_not_have), context.getString(R.string.good), context.getString(R.string.bad), context.getString(R.string.joint_box_a),
                () -> doShieldFields.getOpgwFieldsA().getJointBox(), (s) -> doShieldFields.getOpgwFieldsA().setJointBox(s));
        opgwDoShieldA.setOnCheckedChangeListener(onJointBoxCheckChanged(opgwDoShieldA, aJointBox));
        doShieldA = new GoodBadDataModel("A", doShieldAFieldList, adapter,
                doShieldFields::getConditionA, (s) -> {
            doShieldFields.setConditionA(s);
            reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().setConditionA(s);
            switch (s) {
                case Constants.IS_BAD:
                    reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().setSimeMohafezBadConditionA(aDoShieldBadCondition);
                    break;
                case Constants.IS_GOOD:
                    reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().setSimeMohafezBadConditionA(null);
            }
        }, aDoShieldBadCondition::getDescription, aDoShieldBadCondition::setDescription);
        DoubleCheckBoxDataModel acharKeshiDamperDoShieldA = new DoubleCheckBoxDataModel(context.getString(R.string.achar_keshi), null, context.getString(R.string.damper), null,
                aDoShieldBadCondition::getAcharKeshi, aDoShieldBadCondition::setAcharKeshi, aDoShieldBadCondition::getDamper, aDoShieldBadCondition::setDamper);
        DoubleCheckBoxDataModel lolePeressKafiDoShieldA = new DoubleCheckBoxDataModel(context.getString(R.string.lole_press), null, context.getString(R.string.kafi), null,
                aDoShieldBadCondition::getLoolePress, aDoShieldBadCondition::setLoolePress, aDoShieldBadCondition::getKafi, aDoShieldBadCondition::setKafi);
        DoubleCheckBoxDataModel colompArmoradDoShieldA = new DoubleCheckBoxDataModel(context.getString(R.string.colomp), null, context.getString(R.string.armorad), null,
                aDoShieldBadCondition::getKolomp, aDoShieldBadCondition::setKolomp, aDoShieldBadCondition::getArmorad, aDoShieldBadCondition::setArmorad);
        DoubleCheckBoxDataModel jamperEshpilDoShieldA = new DoubleCheckBoxDataModel(context.getString(R.string.jumper), null, context.getString(R.string.eshpil), null,
                aDoShieldBadCondition::getJamper, aDoShieldBadCondition::setJamper, aDoShieldBadCondition::getEshpil, aDoShieldBadCondition::setEshpil);
        CheckBoxDataModel gooyEkhtarDoShieldA = new CheckBoxDataModel("گوی اخطار",null,aDoShieldBadCondition::getGoyeEkhtar,aDoShieldBadCondition::setGoyeEkhtar);
        GeneralDataModel[] doShieldAFiields = {acharKeshiDamperDoShieldA, lolePeressKafiDoShieldA, colompArmoradDoShieldA, jamperEshpilDoShieldA,gooyEkhtarDoShieldA};
        opgwDoShieldB = new SegmentedControlDataModel(context.getString(R.string.shield_wire), context.getString(R.string.opgw), null,
                () -> doShieldFields.getJenseSimB(),
                (s) -> {
                    doShieldFields.setJenseSimB(s);
                    reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().setJenseSimB(s);
                    if (Objects.equals(s, context.getString(R.string.opgw))) {
                        reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().setOpgwFieldsB(doShieldFields.getOpgwFieldsB());
                    } else if (Objects.equals(s, context.getString(R.string.shield_wire))) {
                        reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().setOpgwFieldsB(null);
                    }
                });
        bJointBox = new SegmentedControlDataModel(context.getString(R.string.dose_not_have), context.getString(R.string.good), context.getString(R.string.bad), context.getString(R.string.joint_box_b),
                () -> doShieldFields.getOpgwFieldsB().getJointBox(), (s) -> doShieldFields.getOpgwFieldsB().setJointBox(s));
        opgwDoShieldB.setOnCheckedChangeListener(onJointBoxCheckChanged(opgwDoShieldB, bJointBox));
        doShieldB = new GoodBadDataModel("B", doShieldBFieldList, adapter,
                doShieldFields::getConditionB, (s) -> {
            doShieldFields.setConditionB(s);
            reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().setConditionB(s);
            switch (s) {
                case Constants.IS_BAD:
                    reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().setSimeMohafezBadConditionB(bDoShieldBadCondition);
                    break;
                case Constants.IS_GOOD:
                    reviewModel.getSimeMohafezVaMolhaghat().getDoShieldFields().setSimeMohafezBadConditionB(null);
            }
        }, bDoShieldBadCondition::getDescription, bDoShieldBadCondition::setDescription);
        DoubleCheckBoxDataModel acharKeshiDamperDoShieldB = new DoubleCheckBoxDataModel(context.getString(R.string.achar_keshi), null, context.getString(R.string.damper), null,
                bDoShieldBadCondition::getAcharKeshi, bDoShieldBadCondition::setAcharKeshi, bDoShieldBadCondition::getDamper, bDoShieldBadCondition::setDamper);
        DoubleCheckBoxDataModel lolePeressKafiDoShieldB = new DoubleCheckBoxDataModel(context.getString(R.string.lole_press), null, context.getString(R.string.kafi), null,
                bDoShieldBadCondition::getLoolePress, bDoShieldBadCondition::setLoolePress, bDoShieldBadCondition::getKafi, bDoShieldBadCondition::setKafi);
        DoubleCheckBoxDataModel colompArmoradDoShieldB = new DoubleCheckBoxDataModel(context.getString(R.string.colomp), null, context.getString(R.string.armorad), null,
                bDoShieldBadCondition::getKolomp, bDoShieldBadCondition::setKolomp, bDoShieldBadCondition::getArmorad, bDoShieldBadCondition::setArmorad);
        DoubleCheckBoxDataModel jamperEshpilDoShieldB = new DoubleCheckBoxDataModel(context.getString(R.string.jumper), null, context.getString(R.string.eshpil), null,
                bDoShieldBadCondition::getJamper, bDoShieldBadCondition::setJamper, bDoShieldBadCondition::getEshpil, bDoShieldBadCondition::setEshpil);
        CheckBoxDataModel gooyEkhtarDoShieldB = new CheckBoxDataModel("گوی اخطار",null,bDoShieldBadCondition::getGoyeEkhtar,bDoShieldBadCondition::setGoyeEkhtar);
        GeneralDataModel[] doShieldBFields = {acharKeshiDamperDoShieldB, lolePeressKafiDoShieldB, colompArmoradDoShieldB, jamperEshpilDoShieldB, gooyEkhtarDoShieldB};
        takShieldAFieldList.clear();
        takShieldAFieldList.addAll(Arrays.asList(takShieldAFields));
        doShieldAFieldList.clear();
        doShieldAFieldList.addAll(Arrays.asList(doShieldAFiields));
        doShieldBFieldList.clear();
        doShieldBFieldList.addAll(Arrays.asList(doShieldBFields));
        if (Objects.equals(simeMohafezVaMolhaght.getSelectedItemText(), context.getString(R.string.tak_shield)))
            addTakShieldItems();
        else if (Objects.equals(simeMohafezVaMolhaght.getSelectedItemText(), context.getString(R.string.do_shield)))
            addDoshieldItems();
    }

    private void laneParandeVaAshiaEzafeFieldsFilling() {
        ProjectModel.LaneParandeBadCondition laneParandeBadCondition = reviewModelHelper.getLaneParande().getBadCondition();
        ProjectModel.AshiaEzafeBadCondition ashiaEzafeBadCondition = reviewModelHelper.getAshiaEzafe().getBadCondition();
        laneParande = new GoodBadDataModel(context.getString(R.string.lane_parande), laneParandeFieldsList, adapter,
                () -> reviewModelHelper.getLaneParande().getCondition(),
                (s) -> {
                    reviewModelHelper.getLaneParande().setCondition(s);
                    reviewModel.getLaneParande().setCondition(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            reviewModel.getLaneParande().setBadCondition(reviewModelHelper.getLaneParande().getBadCondition());
                            break;
                        case Constants.IS_GOOD:
                            reviewModel.getLaneParande().setBadCondition(null);
                            break;
                    }
                },
                laneParandeBadCondition::getDescription, laneParandeBadCondition::setDescription);
        laneParande.setGoodButtonText(context.getString(R.string.dose_not_exist));
        laneParande.setBadButtonText(context.getString(R.string.exist));
        NumberEditViewDataModel tedadeLaneParandeGard = new NumberEditViewDataModel(context.getString(R.string.tedade_lane_parande_gard),
                laneParandeBadCondition::getTedadeLaneParandeGard, laneParandeBadCondition::setTedadeLaneParandeGard);
        NumberEditViewDataModel tedadeLaneParandeFase = new NumberEditViewDataModel(context.getString(R.string.tedade_lane_parande_fase),
                laneParandeBadCondition::getTedadeLaneParandePhase, laneParandeBadCondition::setTedadeLaneParandePhase);
        NumberEditViewDataModel tedadeLaneParandeDakal = new NumberEditViewDataModel(context.getString(R.string.tedade_lane_parande_dakal),
                laneParandeBadCondition::getTedadeLaneParandeDakal, laneParandeBadCondition::setTedadeLaneParandeDakal);
        laneParandeFieldsList.add(tedadeLaneParandeGard);
        laneParandeFieldsList.add(tedadeLaneParandeFase);
        laneParandeFieldsList.add(tedadeLaneParandeDakal);
        ashiaEzafe = new GoodBadDataModel(context.getString(R.string.ashia_ezafe), ashiaEzafeFieldsList, adapter,
                () -> reviewModelHelper.getAshiaEzafe().getCondition(),
                (s) -> {
                    reviewModelHelper.getAshiaEzafe().setCondition(s);
                    reviewModel.getAshiaEzafe().setCondition(s);
                    switch (s) {
                        case Constants.IS_BAD:
                            reviewModel.getAshiaEzafe().setBadCondition(reviewModelHelper.getAshiaEzafe().getBadCondition());
                            break;
                        case Constants.IS_GOOD:
                            reviewModel.getAshiaEzafe().setBadCondition(null);
                            break;
                    }
                },
                ashiaEzafeBadCondition::getDescription, ashiaEzafeBadCondition::setDescription);
        NumberEditViewDataModel tedadeashiaEzafeGard = new NumberEditViewDataModel(context.getString(R.string.tedade_ashia_ezafe_gard),
                ashiaEzafeBadCondition::getTedadeashiaEzafeGard, ashiaEzafeBadCondition::setTedadeashiaEzafeGard);
        NumberEditViewDataModel tedadeashiaEzafeFase = new NumberEditViewDataModel(context.getString(R.string.tedade_ashia_ezafe_fase),
                ashiaEzafeBadCondition::getTedadeashiaEzafePhase, ashiaEzafeBadCondition::setTedadeashiaEzafePhase);
        NumberEditViewDataModel tedadeashiaEzafeDakal = new NumberEditViewDataModel(context.getString(R.string.tedade_ashia_ezafe_dakal),
                ashiaEzafeBadCondition::getTedadeashiaEzafeDakal, ashiaEzafeBadCondition::setTedadeashiaEzafeDakal);
        ashiaEzafeFieldsList.add(tedadeashiaEzafeGard);
        ashiaEzafeFieldsList.add(tedadeashiaEzafeFase);
        ashiaEzafeFieldsList.add(tedadeashiaEzafeDakal);
    }

    private CompoundButton.OnCheckedChangeListener onJointBoxCheckChanged(SegmentedControlDataModel segmentedControlDataModel, SegmentedControlDataModel jointBox) {
        return (buttonView, isChecked) -> {
            if (isChecked) {
                String text = buttonView.getText().toString();
                int position = adapter.getItemsModelList().indexOf(segmentedControlDataModel) + 1;
                if (Objects.equals(text, context.getString(R.string.opgw)) && !adapter.getItemsModelList().contains(jointBox)) {
                    adapter.getItemsModelList().add(position, jointBox);
                    try {
                        adapter.notifyItemInserted(position);
                    } catch (IllegalStateException e) {
                        adapter.setHasItemsChanged(true);
                    }
                } else if (Objects.equals(text, context.getString(R.string.shield_wire)) && adapter.getItemsModelList().contains(jointBox)) {
                    adapter.getItemsModelList().remove(jointBox);
                    try {
                        adapter.notifyItemRemoved(position);
                    } catch (IllegalStateException e) {
                        adapter.setHasItemsChanged(true);
                    }
                }
            }
        };
    }

    @Override
    public View binding(LayoutInflater inflater, @Nullable ViewGroup container) {
        FragmentReviewFourthLevelBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_review_fourth_level, container, false);
        binding.setReviewFourthLevelVM(this);
        recyclerView = binding.reviewFourthLevelRecyclerView;
        return binding.getRoot();
    }

    public ReviewFourthLevelVM() {
        reviewModelHelper = mainActivityVM.getReviewModelHelper();
        reviewModel = mainActivityVM.getReviewModel();
    }

    @Override
    public int getRightIconSource() {
        return R.drawable.ic_red_mission_stop;
    }

    @Override
    public View.OnClickListener getOnRightToolBarIconClickListener() {
        return v -> {
            getRouter().navigateToDialogFragment("", new MissionStopDialogVM(() -> {
                Intent intent = new Intent(getActivity(), LocationService.class);
                getActivity().stopService(intent);

                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                if (notificationManager != null) {
                    notificationManager.cancelAll();
                }

                getRouter().backTo(Constants.HOME_SCREEN_KEY);
                mainActivityVM.setReviewModel(null);
                mainActivityVM.setReviewModelHelper(null);
            }));
        };
    }

    @Override
    String getPageHint() {
        return context.getString(R.string.review_fourth_level_hint);
    }

    //for parcel

    public ReviewFourthLevelVM(Parcel in) {
        super(in);
        reviewModelHelper = mainActivityVM.getReviewModelHelper();
        reviewModel = mainActivityVM.getReviewModel();
    }

    public static Creator<ReviewFourthLevelVM> CREATOR = new Creator<ReviewFourthLevelVM>() {
        @Override
        public ReviewFourthLevelVM createFromParcel(Parcel source) {
            return new ReviewFourthLevelVM(source);
        }

        @Override
        public ReviewFourthLevelVM[] newArray(int size) {
            return new ReviewFourthLevelVM[size];
        }
    };
}
