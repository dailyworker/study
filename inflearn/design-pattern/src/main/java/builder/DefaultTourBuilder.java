package builder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DefaultTourBuilder implements TourPlanBuilder{
    private String title;

    private int nights;

    private int days;

    private LocalDate startAt;

    private String whereToStay;

    private final List<DetailPlan> plans = new ArrayList<>();

    @Override
    public TourPlanBuilder title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public TourPlanBuilder nightsAndDays(int nights, int days) {
        this.nights = nights;
        this.days = days;
        return this;
    }

    @Override
    public TourPlanBuilder startAt(LocalDate startAt) {
        this.startAt = startAt;
        return this;
    }

    @Override
    public TourPlanBuilder whereToStay(String whereToStay) {
        this.whereToStay = whereToStay;
        return this;
    }

    @Override
    public TourPlanBuilder addPlan(int day, String plan) {
        this.plans.add(new DetailPlan(day, plan));
        return this;
    }

    @Override
    public TourPlan getPlan() {
        return new TourPlan(title, nights, days, startAt, whereToStay, plans);
    }
}
