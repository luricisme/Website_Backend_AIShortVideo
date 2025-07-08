package com.cabybara.aishortvideo.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoSearchQueryCriteriaConsumer implements Consumer<SearchCriteria> {
    private final List<Predicate> predicates = new ArrayList<>();
    private CriteriaBuilder builder;
    private Root root;

    @Override
    public void accept(SearchCriteria param) {
        Predicate condition = null;

        if (param.getOperation().equalsIgnoreCase(">")) {
            condition = builder.greaterThanOrEqualTo(
                    root.get(param.getKey()), param.getValue().toString());
        } else if (param.getOperation().equalsIgnoreCase("<")) {
            condition = builder.lessThanOrEqualTo(
                    root.get(param.getKey()), param.getValue().toString());
        } else if (param.getOperation().equalsIgnoreCase(":")) {
            if (root.get(param.getKey()).getJavaType() == String.class) {
                condition = builder.like( builder.lower(root.get(param.getKey())), "%" + param.getValue().toString().toLowerCase() + "%");
            } else {
                condition = builder.equal(root.get(param.getKey()), param.getValue());
            }
        }

        if (condition != null) {
            predicates.add(condition);
        }
    }

    public Predicate getPredicate() {
        return builder.or(predicates.toArray(new Predicate[0]));
    }
}
