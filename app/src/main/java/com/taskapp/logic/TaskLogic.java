package com.taskapp.logic;

import java.time.LocalDate;
import java.util.List;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;
import com.taskapp.model.Task;
import com.taskapp.model.User;

public class TaskLogic {
    private final TaskDataAccess taskDataAccess ;
    private final LogDataAccess logDataAccess ;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        List<Task> tasks = taskDataAccess.findAll();
        
        tasks.forEach(task ->{
            String status = "";
            switch (task.getStatus()) {
                case 0:
                    status = "未着手";
                    break;
                case 1:
                    status = "着手中";
                    break;
                case 2:
                    status = "完了";
                    break;
            }
            //担当者の名前を取得
            String assigneeName = task.getRepUser().getName();

            if (task.getRepUser().getCode() == loginUser.getCode()) {
                System.out.println(task.getCode() + ". " + "タスク名：" + task.getName() + ", 担当者名：あなたが担当しています" + ", ステータス：" + status);
            } else {
                System.out.println(task.getCode() + ". " + "タスク名：" + task.getName() + ", 担当者名：" + assigneeName + "が担当しています" + ", ステータス：" + status);
            }
        });
    }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
                    User loginUser) throws AppException {
        //Rep_User_Codeの取得
        User repUser = userDataAccess.findByCode(repUserCode);
        if (repUser == null){
            throw new AppException("存在するユーザーコードを入力してください");
        }
        //新しくtaskオブジェクトを生成する
        Task task = new Task(code, name, 0, repUser);
        //新しくlogオブジェクトを生成する
        Log log = new Log(code, loginUser.getCode(), 0, LocalDate.now());
        //task.csvにデータを一件新規登録する
        taskDataAccess.save(task);
        //log.csvにデータを一件新規登録する
        logDataAccess.save(log);

        System.out.println(task.getName() + "の登録が完了しました");
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int statusInt,
                            User loginUser) throws AppException {
        Task task = taskDataAccess.findByCode(code);
        //task.csvに存在しない時
        if(task == null ){
            throw new AppException("存在するタスクコードを入力してください");
        }
        //tasks.csvに存在するタスクのステータスが、変更後のステータスの1つ前じゃない場合
        if(task.getStatus() + 1 != statusInt){
            throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
        }

        //新しくタスクデータをセットする
        task.setStatus(statusInt);
        //tasks.csvのデータを更新する
        taskDataAccess.update(task);

        //新しくLogオブジェクトを生成する
        Log log = new Log(code, loginUser.getCode(), statusInt, LocalDate.now());
        //log.csvにデータを一件新規登録する
        logDataAccess.save(log);

        System.out.println( "ステータスの変更が完了しました。");
    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
    // }
}