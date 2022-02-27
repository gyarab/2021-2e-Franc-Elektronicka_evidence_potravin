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
    int opakovani = 0;

    public SynchronizaceService(HlavniOknoController controller) {
        this.controller = controller;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                Platform.runLater(() -> {
                    if (opakovani == 0) {
                        boolean aktualizovat = false;
                        if(OfflineData.zmena == true){
                            aktualizovat = true;
                        }
                        Runnable synchronizaceRunnable = () -> {
                            OfflineData.Synchronizovat();
                        };
                        Thread synchronizaceThread = new Thread(synchronizaceRunnable);
                        synchronizaceThread.start();
                        controller.indikator.setText(OfflineData.online == true ? "ONLINE" : "OFFLINE");
                        // Pokud došlo ke změně v seznamu potravin, aktualizuj jejich zobrazení.
                        if (OfflineData.zmena == true || aktualizovat) {
                            System.out.println("Update náhledu");
                            controller.zobraz();
                        }
                        opakovani++;
                    }else{
                        opakovani ++;
                        if(opakovani >= 5){
                            opakovani = 0;
                        }
                        if (OfflineData.zmena == true) {
                            OfflineData.zmena = false;
                            System.out.println("Update náhledu");
                            controller.zobraz();
                        }
                    }
                });
                return null;
            }
        };
    }
    

}
