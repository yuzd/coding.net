package coding.net.plugin.webhook;

/**
 * Created by Administrator on 2016/9/11 0011.
 */
public enum CDEvent {
    PUSH,
    MERGE_REQUEST,
    PULL_REQUEST,
    PING;

    private CDEvent() {
    }
}
