package utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static <T> List<T> sublist(List<T> list, int start, int len) {
        int size = list.size();
        if (start + len <= size) {
            return list.subList(start, start + len);
        } else {
            List<T> answer = new ArrayList<>(list.subList(start, size));
            answer.addAll(list.subList(0, len - (size - start)));
            return answer;
        }
    }

    public static List<List<Pair>> joinTwoGlues(List<List<Pair>> firstPart, List<List<Pair>> secondPart, List<Pair> commonGlue) {
        List<List<Pair>> allAnswers = new ArrayList<>();
        if (firstPart.isEmpty()) {
            if (secondPart.isEmpty()) {
                allAnswers.add(new ArrayList<>());
                allAnswers.get(0).addAll(commonGlue);
            } else {
                for (List<Pair> oneGlue : secondPart) {
                    oneGlue.addAll(commonGlue);
                }
                allAnswers.addAll(secondPart);
            }
        } else {
            if (secondPart.isEmpty()) {
                for (List<Pair> oneGlue : firstPart) {
                    oneGlue.addAll(commonGlue);
                }
                allAnswers.addAll(firstPart);
            } else {
                List<List<Pair>> answer = new ArrayList<>();
                for (int j = 0; j < firstPart.size() * secondPart.size(); j++) {
                    List<Pair> partAnswer = new ArrayList<>();
                    partAnswer.addAll(commonGlue);
                    partAnswer.addAll(firstPart.get(j / secondPart.size()));
                    partAnswer.addAll(secondPart.get(j % secondPart.size()));
                    answer.add(partAnswer);
                }
                allAnswers.addAll(answer);
            }
        }
        return allAnswers;
    }
}
