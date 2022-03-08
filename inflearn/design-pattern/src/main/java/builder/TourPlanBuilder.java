package builder;

import java.time.LocalDate;

public interface TourPlanBuilder {
    TourPlanBuilder title(String title);
    TourPlanBuilder nightsAndDays(int nights, int days);
    TourPlanBuilder startAt(LocalDate startAt);
    TourPlanBuilder whereToStay(String whereToStay);
    TourPlanBuilder addPlan(int day, String plan);
    TourPlan getPlan();
}
