package com.controladad.boutia_pms.fragment_navigation_handler;

import android.content.Context;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.controladad.boutia_pms.fragments.GeneralDialogFragment;
import com.controladad.boutia_pms.fragments.GeneralFragment;
import com.controladad.boutia_pms.utility.BoutiaApplication;
import com.controladad.boutia_pms.view_models.GeneralVM;
import com.controladad.boutia_pms.view_models.MainActivityVM;

import javax.inject.Inject;

public class FragmentNavigator {
    private FragmentManager fragmentManager;
    private int containerId;
    private FragmentRouter router;
    private boolean isNavigationOnSubFragments = false;
    @Inject
    MainActivityVM mainActivityVM;
    @Inject
    Context context;
    private String routerKey;

    public FragmentNavigator(FragmentManager fragmentManager, int containerId, String routerKey) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.routerKey = routerKey;
        BoutiaApplication.INSTANCE.getAppComponents().inject(this);
        this.router = mainActivityVM.getRouter(routerKey);
    }

    public FragmentNavigator(FragmentManager fragmentManager, int containerId, FragmentRouter router, boolean isNavigationOnSubFragments) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.router = router;
        this.isNavigationOnSubFragments = isNavigationOnSubFragments;
        BoutiaApplication.INSTANCE.getAppComponents().inject(this);
    }


    private Fragment createFragment(GeneralVM viewModel) {
        if (!isNavigationOnSubFragments) return GeneralFragment.newInstance(viewModel, routerKey);
        return GeneralFragment.newInstance(viewModel, routerKey);
    }

    private DialogFragment createDialogFragment(GeneralVM viewModel) {
        return GeneralDialogFragment.newInstance(viewModel);
    }


    private void exit() {
        if (mainActivityVM.getActivity() != null) mainActivityVM.getActivity().finish();
    }


    private void setupFragmentTransactionAnimation(Command command,
                                                   Fragment currentFragment,
                                                   Fragment nextFragment,
                                                   FragmentTransaction fragmentTransaction) {
        //fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
    }

    void applyCommand(Command command) {
        if (command instanceof Forward) {
            Forward forward = (Forward) command;
            Fragment fragment = createFragment(forward.getTransitionData());
            if (fragment == null) {
                unknownScreen(command);
                return;
            }

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            setupFragmentTransactionAnimation(
                    command,
                    fragmentManager.findFragmentById(containerId),
                    fragment,
                    fragmentTransaction
            );


            fragmentTransaction
                    .replace(containerId, fragment)
                    .addToBackStack(forward.getScreenKey())
                    .commit();

            router.setChainOrder(router.getChainOrder() + 1);

        } else if (command instanceof Back) {
            if (fragmentManager.getBackStackEntryCount() > 0 && router.getChainOrder() > 1) {

                fragmentManager.popBackStackImmediate();
            } else {
                exit();
            }
            router.setChainOrder(router.getChainOrder() - 1);
        } else if (command instanceof Replace) {
            mainActivityVM.setShouldLoaderBeShown(true);
//            mainActivityVM.showLoader();
            Replace replace = (Replace) command;
            router.setReplacedGeneralVM(replace.getTransitionData());
            Fragment fragment = createFragment(replace.getTransitionData());
            if (fragment == null) {
                unknownScreen(command);
                return;
            }
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                setupFragmentTransactionAnimation(
                        command,
                        fragmentManager.findFragmentById(containerId),
                        fragment,
                        fragmentTransaction
                );


                fragmentTransaction
                        .replace(containerId, fragment)
                        .addToBackStack(replace.getScreenKey())
                        .commit();
            } else {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                setupFragmentTransactionAnimation(
                        command,
                        fragmentManager.findFragmentById(containerId),
                        fragment,
                        fragmentTransaction
                );

                fragmentTransaction
                        .replace(containerId, fragment)
                        .commit();
            }
        } else if (command instanceof BackTo) {
            String key = ((BackTo) command).getScreenKey();

            if (key == null) {
                backToRoot();
            } else {
                int j = 0;
                boolean hasScreen = false;
                for (int i = fragmentManager.getBackStackEntryCount() - 1; i >= 0; i--) {
                    if (key.equals(fragmentManager.getBackStackEntryAt(i).getName())) {
                        fragmentManager.popBackStackImmediate(key, 0);
                        hasScreen = true;
                        break;
                    }
                    j++;
                }

                router.setChainOrder(router.getChainOrder() - j);

                if (!hasScreen) {
                    backToRoot();
                }
            }
        } else if (command instanceof ForwardToDialogFragment) {
            ForwardToDialogFragment forward = (ForwardToDialogFragment) command;
            DialogFragment fragment = createDialogFragment(forward.getTransitionData());
            fragment.show(fragmentManager, forward.getScreenKey());
        } else if (command instanceof ForwardWithSharedElements) {
            ForwardWithSharedElements forward = (ForwardWithSharedElements) command;
            Fragment fragment = createFragment(forward.getTransitionData());
            fragmentManager.beginTransaction()
                    .addSharedElement(forward.getSharedElement(), forward.getTransitionName())
                    .addToBackStack(forward.getScreenKey())
                    .replace(containerId, fragment)
                    .commit();
            router.setChainOrder(router.getChainOrder() + 1);
        } else if (command instanceof AddSubFragment) {
            AddSubFragment forward = (AddSubFragment) command;
            FragmentRouter subFragmentRouter;
            if (router.getChildRouter() == null) {
                subFragmentRouter = new FragmentRouter();
                router.setChildRouter(subFragmentRouter);
            } else subFragmentRouter = router.getChildRouter();
            FragmentNavigator navigator = new FragmentNavigator(forward.getParentFragment().getChildFragmentManager(), forward.getLayoutId(), subFragmentRouter, true);
            subFragmentRouter.setNavigator(navigator);
            Fragment fragment = GeneralFragment.newInstance(forward.getTransitionData(), routerKey);
            if (fragment == null) {
                unknownScreen(command);
                return;
            }

            FragmentTransaction fragmentTransaction = forward.getParentFragment().getChildFragmentManager().beginTransaction();


            fragmentTransaction
                    .replace(forward.getLayoutId(), fragment)
                    .commit();

        }

    }

    private void backToRoot() {
        router.setChainOrder(1);
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


    private void unknownScreen(Command command) {
        if (mainActivityVM.getActivity() != null) mainActivityVM.getActivity().recreate();
    }

}
