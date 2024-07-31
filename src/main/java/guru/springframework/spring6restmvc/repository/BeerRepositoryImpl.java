package guru.springframework.spring6restmvc.repository;

import guru.springframework.spring6restmvc.domain.Beer;
import guru.springframework.spring6restmvc.model.BeerSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * @author john
 * @since 29/07/2024
 */
    public class BeerRepositoryImpl implements BeerRepositoryCustom {

    private EntityManager entityManager;

    public BeerRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Beer> findBySearchCriteria(BeerSearchCriteria criteria) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Beer> criteriaQuery = criteriaBuilder.createQuery(Beer.class);
        Root<Beer> beerRoot = criteriaQuery.from(Beer.class);
        List<Predicate> predicates = new ArrayList<>();
        if (criteria.getName()!= null) {
            predicates.add(criteriaBuilder.like(beerRoot.get("beerName"), "%" + criteria.getName() + "%"));
        }
        if (criteria.getStyle()!= null) {
            predicates.add(criteriaBuilder.equal(beerRoot.get("beerStyle"), criteria.getStyle()));
        }
        if (criteria.getPriceMin() != null && criteria.getPriceMax() != null) {
            predicates.add(criteriaBuilder.between(beerRoot.get("price"), criteria.getPriceMin(), criteria.getPriceMax()));
        }
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
