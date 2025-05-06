package com.ruoyi.system.domain.aimodel;

import java.util.Map;

public class TaskStatusResponse {
    private Map<String, TaskStatus> tasks;

    // Getter 和 Setter
    public Map<String, TaskStatus> getTasks() {
        return tasks;
    }

    public void setTasks(Map<String, TaskStatus> tasks) {
        this.tasks = tasks;
    }
}
