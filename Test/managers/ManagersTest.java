package managers;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldGetDefaultReturnInMemoryTaskManager() {






        TaskManager manager = Managers.getDefault();
        TaskManager manager1 = new InMemoryTaskManager();
        assertTrue(manager.getClass().isInstance(manager1), "Объект возвращается не корректно");


    }


    @Test
    void shouldGetDefaultHistoryReturnInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        HistoryManager historyManager1 = new InMemoryHistoryManager();
        assertTrue(historyManager.getClass().isInstance(historyManager1), "Объект возвращается не корректно");
    }
}