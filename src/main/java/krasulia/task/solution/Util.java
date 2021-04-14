package krasulia.task.solution;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Util {

    private static List<String> resultList = new ArrayList<>();
    private static int numberOfLine = 0;
    public static final String FORMATTER_PATTERN = "d.MM.yyyy";

    public static String getAverageWaitingTime(String records) {
        List<String> waitingTimelineList = new ArrayList<>();
        String[] arr = Objects.requireNonNull(records.split("\n"));
        numberOfLine = Integer.parseInt(arr[0]);
        for (String someString : arr) {
            addElement(someString, waitingTimelineList);
        }
        return resultList.toString();
    }

    private static void addElement(String someString, List<String> waitingTimelineList) {
        if (someString.startsWith("C")) {
            waitingTimelineList.add(someString);
        } else if (someString.startsWith("D")) {
            waitingTimelineList.stream()
                    .filter(line -> isWaitingTimelineMatchQuery(line, someString))
                    .map(Util::getTimeFromWaitingTimeline)
                    .mapToInt(Integer::valueOf)
                    .average()
                    .ifPresentOrElse(element -> resultList.add(String.valueOf(element)), () -> resultList.add("-"));
        }
    }

    private static boolean isWaitingTimelineMatchQuery(String waitingTimeline, String query) {
        return isLineDateMatching(waitingTimeline, query) &&
                isWaitingTimelineMatchByServiceIdAndQuestionTypeId(waitingTimeline, query);
    }

    private static boolean isLineDateMatching(String waitingTimeline, String query) {
        LocalDate waitingTimelineDate = getDateFromWaitingTimeLine(waitingTimeline);
        LocalDate queryDateFrom;
        String queryDate = getStringAfterResponseType(query);
        if (queryDate.contains("-")) {
            String[] dateArr = queryDate.split("-");
            queryDateFrom = convertStringToLocalDate(dateArr[0], FORMATTER_PATTERN);
            LocalDate queryDateTo = convertStringToLocalDate(dateArr[1], FORMATTER_PATTERN);
            return waitingTimelineDate.isEqual(queryDateFrom) ||
                    waitingTimelineDate.isEqual(queryDateTo) ||
                    waitingTimelineDate.isAfter(queryDateFrom) && waitingTimelineDate.isBefore(queryDateTo);
        } else {
            queryDateFrom = convertStringToLocalDate(queryDate, FORMATTER_PATTERN);
            return waitingTimelineDate.isEqual(queryDateFrom) || waitingTimelineDate.isAfter(queryDateFrom);
        }
    }

    private static boolean isWaitingTimelineMatchByServiceIdAndQuestionTypeId(String waitingTimeline, String query) {
        String[] waitingTimelineBeforeResponseTypeArr = getStringBeforeResponseType(waitingTimeline).split(" ");
        String[] queryBeforeResponseTypeArr = getStringBeforeResponseType(query).split(" ");
        return isIdMatch(queryBeforeResponseTypeArr[1], waitingTimelineBeforeResponseTypeArr[1]) &&
                isIdMatch(queryBeforeResponseTypeArr[2], waitingTimelineBeforeResponseTypeArr[2]);
    }

    private static boolean isIdMatch(String queryServiceOrQuestionTypeId, String waitingTimelineServiceOrQuestionTypeId) {
        return queryServiceOrQuestionTypeId.matches("\\*") ||
                (waitingTimelineServiceOrQuestionTypeId.length() >= queryServiceOrQuestionTypeId.length()
                        && queryServiceOrQuestionTypeId.equals(waitingTimelineServiceOrQuestionTypeId.substring(0, queryServiceOrQuestionTypeId.length())));
    }

    private static String getStringBeforeResponseType(String query) {
        return query.contains("P") ? query.split("P")[0] : query.split("N")[0];
    }

    private static String getStringAfterResponseType(String query) {
        return query.contains("P") ? query.split("P")[1] : query.split("N")[1];
    }

    private static LocalDate getDateFromWaitingTimeLine(String line) {
        return convertStringToLocalDate(
                getStringAfterResponseType(line).trim().split(" ")[0],
                FORMATTER_PATTERN);
    }

    private static String getTimeFromWaitingTimeline(String waitingTimeline) {
        String[] waitingTimelineArr = waitingTimeline.split(" ");
        return waitingTimelineArr[waitingTimelineArr.length - 1];
    }

    private static LocalDate convertStringToLocalDate(String date, String patternFormatter) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patternFormatter);
        return LocalDate.parse(date.trim(), formatter);
    }

}
