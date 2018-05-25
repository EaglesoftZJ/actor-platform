package im.actor.runtime.mvvm;

import com.google.j2objc.annotations.ObjectiveCName;

import java.util.ArrayList;

/**
 * Bindable MVVM Value. Used in UI data binding
 *
 * @param <T> type of value
 */
public abstract class Value<T> {

    private ArrayList<ValueChangedListener<T>> listeners = new ArrayList<>();

    private String name;

    /**
     * Default constructor of value
     *
     * @param name name of Value
     */
    public Value(String name) {
        this.name = name;
    }

    /**
     * Get current value
     *
     * @return the value
     */
    @ObjectiveCName("get")
    public abstract T get();

    /**
     * Getting Name of Value
     * Useful for debugging current bindings and notifications
     *
     * @return name of value
     */
    public String getName() {
        return name;
    }

    /**
     * Subscribe to value updates
     *
     * @param listener update listener
     */
    @ObjectiveCName("subscribeWithListener:")
    public void subscribe(ValueChangedListener<T> listener) {
        subscribe(listener, true);
    }

    /**
     * Subscribe to value updates
     *
     * @param listener update listener
     * @param notify   perform notify about current value
     */
    @ObjectiveCName("subscribeWithListener:notify:")
    public void subscribe(ValueChangedListener<T> listener, boolean notify) {
        // im.actor.runtime.Runtime.checkMainThread();

        if (listeners.contains(listener)) {
            return;
        }
        listeners.add(listener);
        if (notify) {
            listener.onChanged(get(), this);
        }
    }

    /**
     * Remove subscription for updates
     *
     * @param listener update listener
     */
    @ObjectiveCName("unsubscribeWithListener:")
    public void unsubscribe(ValueChangedListener<T> listener) {
        // im.actor.runtime.Runtime.checkMainThread();

        listeners.remove(listener);
    }

    /**
     * Performing notification to subscribers
     *
     * @param value new value
     */
    protected void notify(final T value) {
        System.out.println("EventBus开始1");
        // im.actor.runtime.Runtime.postToMainThread(() -> notifyInMainThread(value));
        im.actor.runtime.Runtime.postToMainThread(() -> {
            System.out.println("EventBus开始");
            for (ValueChangedListener<T> listener : listeners) {
                listener.onChanged(value, Value.this);
            }
            System.out.println("EventBus结束");
        });
        System.out.println("EventBus结束1");
    }

    /**
     * Performing notification to subscribers if we know that we are on mainthread
     * Useful for chainging updates from chain of values
     *
     * @param value new value
     */
    protected void notifyInMainThread(final T value) {
        System.out.println("EventBus开始");
        for (ValueChangedListener<T> listener : listeners) {
            listener.onChanged(value, Value.this);
        }
         System.out.println("EventBus结束");
    }
}
