package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.model.Video;
import com.cabybara.aishortvideo.repository.criteria.SearchCriteria;
import com.cabybara.aishortvideo.repository.criteria.VideoSearchQueryCriteriaConsumer;
import com.cabybara.aishortvideo.utils.VideoStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Page<Video> advancedSearch(Pageable pageable, String... search) {
        // title:A,category:A,style:A,target:A,script:A
        List<SearchCriteria> criteriaList = new ArrayList<>();

        if (search != null && search.length > 0) {
            for (String s : search) {
                System.out.println(s);
                Pattern pattern = Pattern.compile("(\\w+?)(:|>|<)(.*)");
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }
        criteriaList.forEach(c -> {
            System.out.println(c.getValue());
        });


        List<Video> videos = getVideos(criteriaList, pageable);
        long total = countVideos(criteriaList);

        return new PageImpl<>(videos, pageable, total);
    }

    private List<Video> getVideos(List<SearchCriteria> criteriaList, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Video> query = criteriaBuilder.createQuery(Video.class);
        Root<Video> root = query.from(Video.class);

        VideoSearchQueryCriteriaConsumer queryConsumer = new VideoSearchQueryCriteriaConsumer(criteriaBuilder, root);
        criteriaList.forEach(queryConsumer);

        Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), VideoStatus.PUBLISHED);
        Predicate finalPredicate = criteriaBuilder.and(statusPredicate, queryConsumer.getPredicate());
        query.where(finalPredicate);

        return entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())   // offset = pageNumber * pageSize
                .setMaxResults(pageable.getPageSize())       // limit
                .getResultList();
    }

    private long countVideos(List<SearchCriteria> criteriaList) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Video> root = countQuery.from(Video.class);

        VideoSearchQueryCriteriaConsumer consumer = new VideoSearchQueryCriteriaConsumer(cb, root);
        criteriaList.forEach(consumer);

        Predicate statusPredicate = cb.equal(root.get("status"), VideoStatus.PUBLISHED);
        Predicate finalPredicate = cb.and(statusPredicate, consumer.getPredicate());
        countQuery.select(cb.count(root)).where(finalPredicate);

//        countQuery.select(cb.count(root)).where(consumer.getPredicate());

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
