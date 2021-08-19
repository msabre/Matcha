package usecase;

import domain.entity.FilterParams;
import usecase.port.FilterParamsRepository;

public class UpdateFilter {
    FilterParamsRepository filterParamsRepository;

    public void update(FilterParams filterParams) {
        filterParamsRepository.update(filterParams);
    }

    public UpdateFilter(FilterParamsRepository filterParamsRepository) {
        this.filterParamsRepository = filterParamsRepository;
    }
}
