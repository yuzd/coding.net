package coding.net.plugin.misc;

import com.google.common.base.Predicate;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Administrator on 2016/9/11 0011.
 */
public abstract class NullSafePredicate<T> implements Predicate<T> {

    @Override
    public boolean apply(T input) {
        return applyNullSafe(checkNotNull(input, "Argument for this predicate can't be null"));
    }

    /**
     * This method will be called inside of {@link #apply(Object)}
     */
    protected abstract boolean applyNullSafe(@Nonnull T input);
}