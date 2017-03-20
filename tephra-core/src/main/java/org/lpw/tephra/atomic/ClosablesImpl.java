package org.lpw.tephra.atomic;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.atomic.closables")
public class ClosablesImpl implements Closables {
    @Inject
    private Optional<Set<Closable>> closables;

    @Override
    public void close() {
        closables.ifPresent(set -> set.forEach(Closable::close));
    }
}
