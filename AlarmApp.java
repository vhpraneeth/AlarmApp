import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AlarmApp{
   JSpinner pickTimeSpinner;
   Date timeValue;
   JButton setAlarmOnButton;
   JButton setAlarmOffButton;
   SimpleDateFormat timeFormat;
   Boolean isLive;
   Thread mainThread;

   //construct AlarmApp GUI
   public AlarmApp(){
      JFrame mainAppFrame = new JFrame("AlarmApp");
      mainAppFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JPanel mainAppPanel = new JPanel();
      mainAppPanel.setLayout(new BoxLayout(mainAppPanel,BoxLayout.Y_AXIS));

      SpinnerDateModel model = new SpinnerDateModel();
      model.setCalendarField(Calendar.SECOND);
      pickTimeSpinner = new JSpinner();
      pickTimeSpinner.setModel(model);
      pickTimeSpinner.setEditor(new JSpinner.DateEditor(pickTimeSpinner, "HH:mm:ss"));
      String startTime = "00:00:00";
      timeFormat = new SimpleDateFormat("HH:mm:ss");
      try{
         Date spinnerStartTime = timeFormat.parse(startTime);
         pickTimeSpinner.setValue(spinnerStartTime);
      }
      catch(ParseException ex){
         ex.printStackTrace();
      }

      setAlarmOnButton = new JButton("Start");
      setAlarmOffButton = new JButton("Stop");
      setAlarmOffButton.setEnabled(false);

      setAlarmOnButton.addActionListener(new alarmOnListener());
      setAlarmOffButton.addActionListener(new alarmOffListener());

      Container containerForSpinner = new Container();
      containerForSpinner.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
      containerForSpinner.add(pickTimeSpinner);
      containerForSpinner.add(setAlarmOnButton);
      containerForSpinner.add(setAlarmOffButton);

      mainAppPanel.add(containerForSpinner);
      mainAppFrame.getContentPane().add(BorderLayout.CENTER, mainAppPanel);
      mainAppFrame.setSize(250,60);
      mainAppFrame.setVisible(true);
   }

   public static void main(String[] args){
      AlarmApp app = new AlarmApp();
   }

   class Timer implements Runnable{
      SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
      Date timeValue;
      Calendar calendarHelper = Calendar.getInstance();
      String newTime;

      public Timer(Date startTime) {
         timeValue = startTime;
         calendarHelper.setTime(timeValue);
         newTime = timeFormat.format(calendarHelper.getTime());
         isLive = true;
      }

      public void run(){
         while(!(newTime.equals("00:00:00")) && isLive){
            try{
               Thread.sleep(1000);
               calendarHelper.add(Calendar.SECOND, -1);
               newTime = timeFormat.format(calendarHelper.getTime());
               pickTimeSpinner.setValue(timeFormat.parse(newTime));
            }
            catch(InterruptedException ex){
            }
            catch(ParseException ex){
               ex.printStackTrace();
            }
         }
         playAlarmSound(newTime);
      }
   }

   //Listener for setAlarmOnButton
   class alarmOnListener implements ActionListener{
      public void actionPerformed(ActionEvent event){
         pickTimeSpinner.setEnabled(false);
         setAlarmOnButton.setEnabled(false);
         setAlarmOffButton.setEnabled(true);

         timeValue = (Date) pickTimeSpinner.getValue();
         Runnable timerThread = new Timer(timeValue);
         mainThread = new Thread(timerThread);
         mainThread.start();
      }
   }

   //Listener for setAlarmOffButton
   class alarmOffListener implements ActionListener{
      public void actionPerformed(ActionEvent event){
         pickTimeSpinner.setEnabled(true);
         setAlarmOnButton.setEnabled(true);
         setAlarmOffButton.setEnabled(false);
         isLive = false;
         mainThread.interrupt();

      }
   }

   public void playAlarmSound(String endTime){
      if(endTime.equals("00:00:00")){
         try{
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("AlarmSound.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
         }
         catch(Exception ex){
         }
      }
   }
}
