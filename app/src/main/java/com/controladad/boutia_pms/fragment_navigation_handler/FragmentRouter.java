package com.controladad.boutia_pms.fragment_navigation_handler;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.controladad.boutia_pms.view_models.GeneralVM;

import lombok.Getter;
import lombok.Setter;

public class FragmentRouter implements Parcelable {
    private CommandBuffer commandBuffer;

    public FragmentRouter() {
    }

    private CommandBuffer getCommandBuffer() {
        if(commandBuffer==null) {
            commandBuffer = new CommandBuffer();
            commandBuffer.setNavigator(navigator);
        }
        return commandBuffer;
    }

    protected void executeCommand(Command command) {
        getCommandBuffer().executeCommand(command);
    }


    public void navigateTo(String screenKey, GeneralVM viewModel) {
        executeCommand(new Forward(screenKey, viewModel));
    }

    public void newScreenChain(String screenKey) {
        newScreenChain(screenKey, null);
    }

    public void newScreenChain(String screenKey, GeneralVM viewModel) {
        executeCommand(new BackTo(null));
        executeCommand(new Forward(screenKey, viewModel));
    }


    public void newRootScreen(String screenKey, GeneralVM viewModel) {
        executeCommand(new BackTo(null));
        executeCommand(new Replace(screenKey, viewModel));
    }




    public void backTo(String screenKey) {
        executeCommand(new BackTo(screenKey));
    }


    public void finishChain() {
        executeCommand(new BackTo(null));
        executeCommand(new Back());
    }

    @Setter
    @Getter
    private FragmentRouter childRouter;

    @Getter
    @Setter
    private GeneralVM replacedGeneralVM;

    @Getter
    private int chainOrder;


    @Getter
    private FragmentNavigator navigator;

    public void setNavigator(FragmentNavigator navigator) {
        this.navigator = navigator;
        getCommandBuffer().setNavigator(navigator);
    }

    public void removeNavigator(){
        navigator = null;
        getCommandBuffer().removeNavigator();
    }


    public void setChainOrder(int chainOrder) {
        this.chainOrder = chainOrder;
    }


    public void navigateToDialogFragment(String screenKey, GeneralVM data){
        executeCommand(new ForwardToDialogFragment(screenKey, data));
    }

    public void navigateToWithSharedElements(String screenKey, GeneralVM data, View sharedElement){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            executeCommand(new ForwardWithSharedElements(screenKey,data,sharedElement.getTransitionName(),sharedElement));
        }
        else executeCommand(new Forward(screenKey,data));
    }

    public void addSubFragment(String screenKey, GeneralVM transitionData, int layoutId, Fragment parentFragment){
        executeCommand(new AddSubFragment(screenKey,transitionData,layoutId, parentFragment));
    }

    public void exit() {
        executeCommand(new Back());
        childRouter = null;
    }

    public void subFragmentExit(){
        subFragmentExit(this);
    }

    public void subFragmentExit(FragmentRouter router){
        if(router.getChildRouter()!=null && router.getChildRouter().getChainOrder()!=0){
            router.getChildRouter().subFragmentExit(router.getChildRouter());
        }
        else exit();
    }


    public void replaceScreen(String screenKey, GeneralVM viewModel) {
        executeCommand(new Replace(screenKey, viewModel));
        childRouter = null;
    }


    //fragment life cycle handler

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //   dest.writeParcelable(childRouter, flags);
        dest.writeInt(chainOrder);
    }

    protected FragmentRouter(Parcel in) {
        // childRouter = in.readParcelable(FragmentRouter.class.getClassLoader());
        chainOrder = in.readInt();
    }

    public static final Creator<FragmentRouter> CREATOR = new Creator<FragmentRouter>() {
        @Override
        public FragmentRouter createFromParcel(Parcel in) {
            return new FragmentRouter(in);
        }

        @Override
        public FragmentRouter[] newArray(int size) {
            return new FragmentRouter[size];
        }
    };


}
