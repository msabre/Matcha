package usecase.port;

import domain.entity.FilterParams;
import domain.entity.Link;

public interface FilterParamsRepository {
    void update(FilterParams filter);

    FilterParams findById(Integer id);
}
