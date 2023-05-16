package com.example.java_course_lab_2;

import javafx.scene.SubScene;

import java.util.ArrayList;

public class Model {
    // Игроки, что подключились
    ArrayList<ClientInfo> allPlayers = new ArrayList<>();

    // Лидеры
    ArrayList<ClientInfo> leaders = new ArrayList<>();

    // Лист готовых игроков
    ArrayList<String> allReadyPlayers = new ArrayList<>();
    // Хранятся расположение наконечников стрел
    ArrayList<MyPoint> nibsAllArrows = new ArrayList<>();
    // Хранятся основания стрел
    ArrayList<MyPoint> sticksAllArrows = new ArrayList<>();
    // Расположение мишеней
    ArrayList<Target> allTargets = new ArrayList<>();

    // Ссылка на объект
    DB_jdbc db;

    // Наблюдатели
    ArrayList<IObserver> allObservers = new ArrayList<>();

    private String winner = null;

    private final ArrayList<String> waitingList = new ArrayList<>();
    private final ArrayList<String> shootingList = new ArrayList<>();

    private volatile boolean isGameStarted = false;

    private boolean isAlreadyGame = false;

    // Конец поля
    int finalPosX = 652, startPosX = 106;
    int[] positionsYNibsArrows = {88, 218, 352, 489};

    // Метод иниициализирует подключение к базе данных
    public void init(DB_jdbc db) {
        this.db = db;
        // Тестируем базу данных
        System.out.println(db.getAllUserInfo());
    }

    // Обновить фрейм для каждого наблюдателя
    public void update() {
        for (IObserver observer: allObservers) {
            observer.update();
        }
    }

    // Обработка нажатия кнопки готов
    public void clickedReady(MainServer mainServer, String userName) {
        if (allReadyPlayers.isEmpty()) {
            allReadyPlayers.add(userName);
            return;
        }

        if (allReadyPlayers.contains(userName)) {
            // Ничего не делаем
        } else {
            allReadyPlayers.add(userName);
        }

        if (allPlayers.size() > 1 && allReadyPlayers.size() == allPlayers.size() && isAlreadyGame == false) {
            System.out.println("Начинаем игру трудяги");
            isAlreadyGame = true;
            isGameStarted = true;
            startGame(mainServer);
        }
    }

    // Обработка нажатия кнопки пауза
    public void clickedPause(String userName) {
        // Должен убираться игрок из готовых
        if (isGameStarted) {
            if (waitingList.contains(userName)) {
                waitingList.remove(userName);
                if (waitingList.size() == 0){
                    int a = 0;
                    synchronized(this) {
                        notifyAll();
                    }
                }
            } else {
                waitingList.add(userName);
            }
        }
    }

    // Обработка нажатия кнопки Лидеры
    public void clickedLeaders(MainServer mainServer) {
        // Должен убираться игрок из готовых
        leaders = db.getAllUserInfo();
        mainServer.broadcast();
        System.out.println("asdsad: " + leaders);
    }



    // Обработка запроса на выстрел
    public void clickedShoot(String userName) {
        if (isGameStarted) {
            // Вот такая проверка, я вообще, думаю, не нужна
            var player = allPlayers.stream()
                    .filter(clientData -> clientData.getPlayerName().equals(userName))
                    .findFirst()
                    .orElse(null);
            assert player != null;
            if (! shootingList.contains(player.getPlayerName())){
                shootingList.add(player.getPlayerName());
                player.addNumberShots(1);
            }
        }
    }

    private void initNumberWins() {
        ArrayList<ClientInfo> temp = db.getAllUserInfo();
        for (ClientInfo allPlayer : allPlayers) {
            ClientInfo currentUser = temp.stream()
                    .filter(clientInfo -> clientInfo.getPlayerName().equals(allPlayer.getPlayerName()))
                    .findFirst()
                    .orElse(null);
            if (currentUser != null) {
                allPlayer.setNumberWins(currentUser.getNumberWins());
            }
        }
    }

    // Логика игры в моделе
    public void startGame(MainServer mainServer) {
        Thread thread = new Thread(() -> {
            int bigTargetMove = 5;
            int smallTargetMove = 10;
            int arrowMove = 5;

            // Функция инициализации числа побед
            initNumberWins();

            while (true) {
                if (!isGameStarted) {
                    winner = null;
                    break;
                }

                // Ждём всех игроков на паузе
                if (waitingList.size() != 0) {
                    synchronized (this) {
                        try {
                            wait();
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }

                if (shootingList.size() != 0) {
                    // Обрабатываем выстрелы
                    for (int i = 0; i < shootingList.size(); i++) {
                        int finalI = i;
                        if (shootingList.get(i) == null) {
                            break;
                        }
                        ClientInfo client = allPlayers.stream()
                                .filter(clientData -> clientData.getPlayerName().equals(shootingList.get(finalI)))
                                .findFirst()
                                .orElse(null);
                        int index = allPlayers.indexOf(client);

                        MyPoint nibPoint = nibsAllArrows.get(index);
                        MyPoint stickPoint = sticksAllArrows.get(index);

                        nibPoint.setX(nibPoint.getX() + arrowMove);
                        stickPoint.setX(stickPoint.getX() + arrowMove);

                        checkShot(nibPoint, stickPoint, client);
                    };
                }

                Target bigTarget = allTargets.get(1);
                Target smallTarget = allTargets.get(0);

                if (smallTarget.getY() <= smallTarget.getRadius() || 580 - smallTarget.getY()  <= smallTarget.getRadius()) {
                    smallTargetMove = -1 * smallTargetMove;
                }
                smallTarget.setY(smallTarget.getY() + smallTargetMove);

                if (bigTarget.getY() <= bigTarget.getRadius() || 580 - bigTarget.getY()  <= bigTarget.getRadius()) {
                    bigTargetMove = -1 * bigTargetMove;
                }
                bigTarget.setY(bigTarget.getY() + bigTargetMove);

                System.out.println("Игра началась выполняю рассылку");
                mainServer.broadcast();

                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {}
            }
        });
        thread.start();
    }

    public synchronized void checkShot(MyPoint nibPoint, MyPoint stickPoint, ClientInfo currentUser) {
        ShootActions shootAction = checkShotAction(nibPoint);

        System.out.println("Вот такой результат выстрела - " + shootAction);

        if (shootAction.equals(ShootActions.FLYING)) {
            return;
        }
        if (shootAction.equals(ShootActions.BIG_SHOT)) {
            currentUser.addNumberScores(1);
        }
        if (shootAction.equals(ShootActions.SMALL_SHOT)) {
            currentUser.addNumberScores(2);
        }

        nibPoint.setX(startPosX);
        stickPoint.setX(startPosX);

        if (shootingList.size() == 1) {
            shootingList.clear();
        }
        else {
            shootingList.remove(currentUser.getPlayerName());
        }
        checkWinner();
    }

    private synchronized void checkWinner() {
        allPlayers.forEach(clientDataManager -> {
            if (clientDataManager.getNumberScores() >= 2) {
                this.winner = clientDataManager.getPlayerName();
                // Перезаписываем базу данных
                clientDataManager.setNumberWins(clientDataManager.getNumberWins() + 1);
                db.writeInfo(clientDataManager);
                gameReset();
                return;
            }
        });
    }

    private void gameReset() {
        sticksAllArrows.forEach(myPoint -> myPoint.setX(startPosX));
        nibsAllArrows.forEach(myPoint -> myPoint.setX(startPosX));


        isGameStarted = false;
        isAlreadyGame = false;
        allReadyPlayers.clear();
//        allTargets.clear();
//        nibsAllArrows.clear();
//        sticksAllArrows.clear();
        waitingList.clear();
        shootingList.clear();
        allPlayers.forEach(ClientInfo::reset);
        this.createStartPos();
    }

    public synchronized ShootActions checkShotAction(MyPoint nibPoint) {
        if (isHit(allTargets.get(1), nibPoint)) {
            return ShootActions.BIG_SHOT;
        }
        if (isHit(allTargets.get(0), nibPoint)) {
            return ShootActions.SMALL_SHOT;
        }
        if (nibPoint.getX() > finalPosX) {
            return ShootActions.MISSING;
        }
        return ShootActions.FLYING;
    }

    public boolean isHit(Target target, MyPoint nib) {
        if (nib.getX() - 20 >= target.getX() - target.getRadius() &&
                nib.getX() - 20 <= target.getX() + target.getRadius() &&
                nib.getY() >= target.getY() - target.getRadius() &&
                nib.getY() <= target.getY() + target.getRadius()) {
            return true;
        }
        return false;
    }

    public void createStartPos() {
        allTargets.add(new Target(513, 428, 20));
        allTargets.add(new Target(371, 178, 40));
//        updateNumberArrows(1);
    }

    public synchronized void updateNumberArrows(int numberPlayer) {
        nibsAllArrows.add(new MyPoint(startPosX, positionsYNibsArrows[numberPlayer - 1]));
        sticksAllArrows.add(new MyPoint(startPosX, positionsYNibsArrows[numberPlayer - 1] - 2));
    }

    public void addClient(ClientInfo clientData) {
        // Добавляем игрока
        allPlayers.add(clientData);
        this.updateNumberArrows(allPlayers.size());
    }

    // Getters
    public ArrayList<Target> getAllTargets() {
        return allTargets;
    }

    public ArrayList<MyPoint> getNibsAllArrows() {
        return nibsAllArrows;
    }

    public ArrayList<MyPoint> getSticksAllArrows() {
        return sticksAllArrows;
    }

    public ArrayList<ClientInfo> getAllPlayers() {
        return allPlayers;
    }
    public ArrayList<ClientInfo> getLeaders() {
        return leaders;
    }


    public String getWinner() {
        return winner;
    }

    // Setters
    public void setAllTargets(ArrayList<Target> allTargets) {
        this.allTargets = allTargets;
    }

    public void setNibsAllArrows(ArrayList<MyPoint> nibsAllArrows) {
        this.nibsAllArrows = nibsAllArrows;
    }

    public void setSticksAllArrows(ArrayList<MyPoint> sticksAllArrows) {
        this.sticksAllArrows = sticksAllArrows;
    }

    public void setAllPlayers(ArrayList<ClientInfo> allPlayers) {
        this.allPlayers = allPlayers;
    }
    public void setLeaders(ArrayList<ClientInfo> leaders) {
        this.leaders = leaders;
    }


    public void setWinner(String winner) {
        this.winner = winner;
    }

    // Добавляем наблюдателя
    public void addObserver(IObserver observer) {
        allObservers.add(observer);
    }
}
