## Java CloudStorage

* Приложение работает на 11-ой Java и Project language level 11    
* Java Fx работает с 11-й версией Java    
* Используется БД SQLite    
____
#### ЗАПУСК JAR
**Через терминал:**    
**Server**    
###### java -jar serverWD.jar
**Client**    
java --module-path "O:\AndroidProject\Java_CloudStorage1\aclientModule\src\main\resources\controllers\SDK\openjfx-11.0.2_windows-x64_bin-sdk\javafx-sdk-11.0.2\lib" --add-modules javafx.controls,javafx.fxml -jar clientWD.jar    
java --module-path "путь скопировать с помощью COPY PATH - Absolute Path и вставить между кавычками находится в aclientModule\src\main\resources\controllers\SDK\openjfx-11.0.2_windows-x64_bin-sdk\javafx-sdk-11.0.2\lib" -jar clientWD.jar    

#### Данные для входа
**Логин: ramis**    
**Пароль: 11111**    


#### Кнопки в основном окне приложения:
* -Кнопка **Upload** загружает файл по имени файла с клиента,
загружает файл в ту директорию в какой находится сервер    
* -Кнопка **Загрузить с клиента на сервер** загружает файл выбором из ListView
клиента, загружает файл в ту директорию в какой находится сервер    
* -Кнопка **Назад** возвращает в корневую директорию сервера
и заодно обновляет данные    
* -Кнопка **+** создает директорию(считывая название с TextField) 
в сервере таким же образом, т.е. в каком месте находишься в той папке и создает 
директорию    
* -Кнопка **Найти** находит файл по части имени файла    
* -Кнопка **Удалить** удаляет файлы или папки с сервера выбором из ListView    
* -Кнопка **Скачать** загружает файлы из сервера в директорию desktop    
* -Кнопка **Корзина** открывает корзину    
* -Кнопка **Удалить файл безвозвратно** удалить файл с концами из корзины    

#### Меню бар в окне приложения:
* -Пункт **Окно входа** переход в окно в авторизации    
* -Пункт **Регистрация** переход в окно регистрации    
* -Пункт **Корзина** открывает корзину    
* -Пункт **Смена пароля** переход в окно смены пароля    
* -Пункт **Удаление аккаунта** удаляет аккаунта    
* -Пункт **Выйти** закрывает приложение    


#### Реализовано:
:white_check_mark: подключение через Netty сервер    
:white_check_mark: копирование, перемещение, удаление, cоздание папок    
:white_check_mark: загрузка, скачивание файлов    
:white_check_mark: AA аутентификация и авторизация    
:white_check_mark: корзина    
:white_check_mark: смена пароля, удаление аккаунта    
:white_check_mark: поиск файлов    
:white_check_mark: ограничение на размер    
:white_check_mark: сортировка файлов    
:white_check_mark: 1 репозиторий - 1 юзер    



