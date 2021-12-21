/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eep;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 *
 * @author xxx
 */
public class SynchronizaceService extends ScheduledService<Void> {

    HlavniOknoController controller;

    public SynchronizaceService(HlavniOknoController controller) {
        this.controller = controller;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    Runnable synchronizaceRunnable = () -> { OfflineData.Synchronizovat();};
                    Thread synchronizaceThread = new Thread(synchronizaceRunnable);
                    synchronizaceThread.start();
                    controller.indikator.setText(OfflineData.online == true ? "ONLINE" : "OFFLINE");
                    // Pokud došlo ke změně v seznamu potravin, aktualizuj jejich zobrazení.
                    if (OfflineData.zmena == true) {
                        System.out.println("Update náhledu");
                        controller.zobraz();
                    }
                });
                return null;
            }
        };
    }

}
