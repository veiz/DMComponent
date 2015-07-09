package com.dimo.share.web.event;

/**
 * Created by Alsor Zhou on 6/11/15.
 */
public class PageEvent {
    PageEventEnum eventType;

    String holder;

    public PageEvent(PageEventEnum eventType, String holder) {
        this.eventType = eventType;
        this.holder = holder;
    }

    public PageEventEnum getEventType() {
        return eventType;
    }

    public String getHolder() {
        return holder;
    }

    public enum PageEventEnum {
        FINISHED(1);

        private int value;

        PageEventEnum(int value) {
            this.value = value;
        }
    }

}
