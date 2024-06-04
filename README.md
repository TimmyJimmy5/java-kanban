# java-kanban

## О проведенных тестах:
Из всех тестов, которые предлагается в ТЗ, выполнены все, кроме следующих:
проверьте, что объект model.Epic нельзя добавить в самого себя в виде подзадачи;
проверьте, что объект model.Subtask нельзя сделать своим же эпиком;
Причина: попытка сделать такое не скомпилируется, поэтому тестировать это нет смысла и нет возможности.

Остальные тесты написаны. Полностью покрывать тестами не стал, ибо это излишне в контексте данной задачи и займет слишком много времени.
Покрыл ключевые методы.

## О правках в коде:
Нашел ошибку метода в изменении задачи (он подставлял нулевой ИД в изменяемую задачу). Пофиксил и проверил.
Подправил метод создания таска, чтобы он возвращал идентификатор при успешном создании и -1 при провале (это касается только сабтаска).
Исправил алгоритм присвоения статуса эпику, т.к. работал некорректно. Теперь всё ок.
