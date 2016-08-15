package suite.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;

/**
 * Created by barocko on 8/14/2016.
 */
public class ToDoMvc {
    private static ElementsCollection tasks = $$("#todo-list li");

    private static SelenideElement newTodo = $("#new-todo");

    @Step
    public static void clearCompleted() {
        $("#clear-completed").click();
    }

    @Step
    public static void add(String... taskTexts) {
        for (String text : taskTexts) {
            newTodo.setValue(text).pressEnter();
        }
    }


    public static void delete(String taskText) {
        tasks.find(exactText(taskText)).hover().$(".destroy").click();
    }

    public static void toggle(String taskText) {
        tasks.find(exactText(taskText)).$(".toggle").click();
    }

    public static void toggleAll() {
        $("#toggle-all").click();
    }

    public static void assertTasksAre(String... taskTexts) {
        tasks.shouldHave(exactTexts(taskTexts));
    }

    public static void assertNoTasks() {
        tasks.shouldBe(empty);
    }

    public static SelenideElement startEdit(String oldTaskText, String newTaskText) {
        tasks.find(exactText(oldTaskText)).doubleClick();
        return tasks.find(cssClass("editing")).find(".edit").setValue(newTaskText);
    }

    public static void cancelEdit(String oldTaskText, String newTaskText) {
        startEdit(oldTaskText, newTaskText).pressEscape();
    }

    public static void confirmEditByPressTab(String oldTaskText, String newTaskText) {
        startEdit(oldTaskText, newTaskText).pressTab();
    }

    public static void confirmEditByClickOutside(String oldTaskText, String newTaskText) {
        startEdit(oldTaskText, newTaskText);
        newTodo.click();
    }

    public static void edit(String oldTaskText, String newTaskText) {
        startEdit(oldTaskText, newTaskText).pressEnter();
    }

    public static void filterActive() {
        $(By.linkText("Active")).click();
    }

    public static void filterCompleted() {
        $(By.linkText("Completed")).click();
    }

    public static void filterAll() {
        $(By.linkText("All")).click();
    }

    public static void assertItemsLeft(Integer count) {
        $("#todo-count>strong").shouldHave(exactText((count.toString())));
    }

    public static void assertNoVisibleTasks() {
        tasks.filter(visible).shouldBe(empty);
    }

    public static void assertVisibleTasksAre(String... taskTexts) {
        tasks.filter(visible).shouldHave(exactTexts(taskTexts));
    }

    public static void ensureAppIsOpened() {
        if (!(url().equals("https://todomvc4tasj.herokuapp.com"))) {
            open("https://todomvc4tasj.herokuapp.com");
        }
    }

    public static void given(Task... tasks) {
        ensureAppIsOpened();
        List<String> taskList = new ArrayList<>();
        for (Task task : tasks) {
            taskList.add("{" + task.taskType + ",\\\"title\\\":\\\"" + task.taskText + "\\\"}");
        }
        executeJavaScript("localStorage.setItem(\"todos-troopjs\",\"[" + String.join(",", taskList) + "]\")");
        refresh();
    }

    public static void givenAtActive(Task... tasks) {
        given(tasks);
        filterActive();
    }

    public static void givenAtCompleted(Task... tasks) {
        given(tasks);
        filterCompleted();
    }

    public static Task[] aTasks(TaskType taskType, String... taskText) {
        Task[] tasks = new Task[taskText.length];
        for (int i = 0; i < taskText.length; i++) {
            tasks[i] = new Task(taskType, taskText[i]);
        }
        return tasks;
    }

    public static Task aTask(TaskType taskType, String taskText) {
        return new Task(taskType, taskText);
    }

    public static void givenAtActive(TaskType taskType, String... taskText) {
        givenAtActive(aTasks(taskType, taskText));
    }

    public static void givenAtAll(TaskType taskType, String... taskText) {
        given(aTasks(taskType, taskText));
    }

    public static void givenAtCompleted(TaskType taskType, String... taskText) {
        givenAtCompleted(aTasks(taskType, taskText));
    }

    public static class Task {
        public String taskText;
        public TaskType taskType;

        public Task(TaskType taskType, String taskText) {
            this.taskText = taskText;
            this.taskType = taskType;
        }

        @Override
        public String toString() {
            return "{" + taskType + ",'title':'" + taskText + "'}";
        }
    }

    public enum TaskType {
        ACTIVE("\\\"completed\\\":false"), COMPLETED("\\\"completed\\\":true");

        public String taskStatus;

        @Override
        public String toString() {
            return taskStatus;

        }

        TaskType(String taskStatus) {
            this.taskStatus = taskStatus;
        }
    }
}
