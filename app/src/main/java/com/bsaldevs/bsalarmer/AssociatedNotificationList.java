package com.bsaldevs.bsalarmer;

import android.app.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azatiSea on 22.08.2018.
 */

public class AssociatedNotificationList {

    private List<Notification> notifications;
    private List<Integer> identificators;

    public AssociatedNotificationList() {
        notifications = new ArrayList<>();
        identificators = new ArrayList<>();
    }

    public void add(Notification notification, int id) {
        notifications.add(notification);
        identificators.add(id);
    }

    public void remove(Notification notification) {
        for (int i = 0; i < notifications.size(); i++) {
            if (notifications.get(i).equals(notification)) {
                notifications.remove(notification);
                identificators.remove(i);
            }
        }
    }

    public void remove(int id) {
        for (int i = 0; i < identificators.size(); i++) {
            if (identificators.get(i) == id) {
                notifications.remove(getNotification(id));
                identificators.remove(i);
            }
        }
    }

    public int getId(Notification notification) {
        int id = -1;
        for (int i = 0; i < notifications.size(); i++) {
            if (notifications.get(i).equals(notification))
                return identificators.get(i);
        }
        return id;
    }

    public Notification getNotification(int id) {
        Notification notification = new Notification();
        for (int i = 0; i < identificators.size(); i++) {
            if (identificators.get(i) == id)
                return notifications.get(i);
        }
        return notification;
    }

    public int size() {
        return notifications.size();
    }

    public Notification getNotificationByIndex(int index) {
        return notifications.get(index);
    }

}
