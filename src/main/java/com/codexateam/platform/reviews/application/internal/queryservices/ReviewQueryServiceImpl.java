package com.codexateam.platform.reviews.application.internal.queryservices;

import com.codexateam.platform.reviews.domain.model.aggregates.Review;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewByIdQuery;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewsByRenterIdQuery;
import com.codexateam.platform.reviews.domain.model.queries.GetReviewsByVehicleIdQuery;
import com.codexateam.platform.reviews.domain.services.ReviewQueryService;
import com.codexateam.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Implementation of ReviewQueryService.
 */
@Service
public class ReviewQueryServiceImpl implements ReviewQueryService {

    private final ReviewRepository reviewRepository;

    public ReviewQueryServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> handle(GetReviewsByVehicleIdQuery query) {
        return reviewRepository.findByVehicleId(query.vehicleId());
    }

    @Override
    public List<Review> handle(GetReviewsByRenterIdQuery query) {
        return reviewRepository.findByRenterId(query.renterId());
    }

    @Override
    public Optional<Review> handle(GetReviewByIdQuery query) {
        return reviewRepository.findById(query.reviewId());
    }
}
