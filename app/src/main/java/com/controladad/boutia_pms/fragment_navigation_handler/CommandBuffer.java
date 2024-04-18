package com.controladad.boutia_pms.fragment_navigation_handler;

import java.util.LinkedList;
import java.util.Queue;

class CommandBuffer {
    private FragmentNavigator navigator;
    private Queue<Command> pendingCommands = new LinkedList<>();


    public void setNavigator(FragmentNavigator navigator) {
        this.navigator = navigator;
        while (!pendingCommands.isEmpty()) {
            if (navigator != null) {
                executeCommand(pendingCommands.poll());
            } else break;
        }
    }


    public void removeNavigator() {
        this.navigator = null;
    }


    public void executeCommand(Command command) {
        if (navigator != null) {
            navigator.applyCommand(command);
        } else {
            pendingCommands.add(command);
        }
    }
}
