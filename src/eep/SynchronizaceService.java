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
 * @author Vojtěch Franc
 * Tato třída má přístup ke třídě Hlavního okna, které vyvolává metodu Task, která zkontroluje, zdali není potřeba náhled zaktualizovat. 
 * Mimo jiné má také na starosti aktualizaci ikonky infirmující o stavu připojení k databázi. 
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
                    OfflineData.synchronizace();
                });
                return null;
            }
        };
    }
    

}
