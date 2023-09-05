# jgit-gui
JGit API gui (javafx)  
![Иллюстрация к проекту](https://media.giphy.com/media/g7GKcSzwQfugw/giphy.gif)  


# Управление файлами

## Описание
Задачей данного курсового проекта является разработка программной системы управления учебными документами высшего учебного заведения.

## Архитектура
В качестве архитектурного стиля для данного курсового проекта был выбран MVC (модель, представление, контроллер). Данный вид построения проекта поможет построить его таким образом, чтобы пользователь, взаимодействуя с представлением, подавал сигнал контроллеру, а контроллер, принимая данный сигнал, по-разному управлял моделью (всеми классами и интерфейсами, находящимися в модели).

## Использованные паттерны
1. Паттерн DAO (Data Access Object):

* Проблема: В приложениях, работающих с базами данных, часто возникает необходимость абстрагировать доступ к данным от специфических деталей реализации базы данных (например, SQL-запросов, соединений и т. д.), чтобы обеспечить более гибкую и легкую поддержку кода.
* Решение: Паттерн DAO предлагает выделить специальный слой (DAO-слоя) между приложением и базой данных. DAO-слой предоставляет унифицированный интерфейс для доступа к данным, скрывая детали конкретной реализации базы данных.
* Применение: DAO-объекты представляют отдельные сущности или модели данных и обеспечивают методы для создания, чтения, обновления и удаления данных (CRUD-операции). Они могут быть использованы в приложениях любого масштаба и сложности, где требуется доступ к базе данных.  
2. Паттерн фабрика для DAO (Factory for DAO):

* Проблема: В приложениях, использующих паттерн DAO, может быть необходимость в создании экземпляров DAO-объектов с различными конфигурациями или на основе разных источников данных.
* Решение: Паттерн фабрика для DAO предлагает вынести процесс создания DAO-объектов в отдельную фабрику, которая инкапсулирует логику создания и конфигурирования экземпляров DAO-объектов.
* Применение: Фабрика для DAO может быть использована, когда требуется гибкость при создании DAO-объектов, например, при работе с разными типами баз данных или при использовании различных настроек доступа к данным.

## Зависимости
Язык разработки Java.
Для создания пользовательского интерфейса используется платформа JavaFX, также была использована база данных PostgreSQL.


![image](https://github.com/tinkivink1/jgit-gui/assets/92641773/f97ad6c7-dcad-447d-88da-1363a5369921)
![image](https://github.com/tinkivink1/jgit-gui/assets/92641773/e6bdbc3e-0f59-472b-83eb-481861af6720)
![image](https://github.com/tinkivink1/jgit-gui/assets/92641773/54825d8d-e583-4efe-a14c-3612220687c0)
![image](https://github.com/tinkivink1/jgit-gui/assets/92641773/48fdd7ce-f05e-4e7a-9568-1ef292a4302e)
