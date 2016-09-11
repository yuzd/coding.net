package coding.net.plugin.misc;

import com.google.common.base.Function;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Administrator on 2016/9/11 0011.
 */
public abstract class NullSafeFunction<F, T> implements Function<F, T> {

    @Override
    public T apply(F input) {
        return applyNullSafe(checkNotNull(input, "This function not allows to use null as argument"));
    }

    /**
     * This method will be called inside of {@link #apply(Object)}
     */
    protected abstract T applyNullSafe(@Nonnull F input);
}