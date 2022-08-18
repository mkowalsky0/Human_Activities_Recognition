<h1 align="center">
  Human Activities Recognition Mobile Application
</h1>

<h2 align="center">
 Hi, I'm Mike. Welcome in my project! 
 <img src="https://media.giphy.com/media/1iqh34Ws3rvJtqoqz5/giphy.gif" width="35px"/>
</h2>

<div id="header" align="center">
  <img src="https://media.giphy.com/media/3orieQ0mh5Mi5vSZZC/giphy.gif" width="300"/>
</div>

<h2 align="center">
 Introduction
</h2>

- :iphone: the mobile application for *Android Systems* (v. 4.0 and higher)

- :man_scientist: using *Convuctional Neural Networks* to classificate motion activities

- :books: using [*Smartphone-Based Recognition of Human Activities and Postural Transitions Data Set*](http://archive.ics.uci.edu/ml/datasets/smartphone-based+recognition+of+human+activities+and+postural+transitions) for the training and testing CNN model

- :vibration_mode: real-time inertial signals registration (based on accelerometer and gyroscope)

<h2 align="center">
 Technologies
</h2>


<div align="center">
  <img src="https://media.giphy.com/media/LMt9638dO8dftAjtco/giphy.gif" title="Python" alt="Python" width="60px"/>
  <img src="https://media.giphy.com/media/SU2ic3wTfuC6JhD1lA/giphy.gif" title="TensorFlow" alt="TensorFlow" width="60px"/>
  <img src="https://github.com/devicons/devicon/blob/master/icons/jupyter/jupyter-original-wordmark.svg" title="Jupyter" alt="Jupyter" width="60px"/>
  <img src="https://github.com/devicons/devicon/blob/master/icons/java/java-original-wordmark.svg" title="Java" alt="Java" width="60px"/>
  <img src="https://github.com/devicons/devicon/blob/master/icons/androidstudio/androidstudio-original.svg" title="AndroidStudio" alt="AndroidStudio" width="60px"/>
</div>

  - *Python v. 3.10.1*
  - *TensorFlow v. 2.9.1 n Keras*
  - *Jupyter Notebook*
  - *Java v. 8*
  - *Android Studio v. 2020.3.1*

<h2 align="center">
 Description
</h2>

This project has three main steps:

1. [Signal Processing](https://github.com/mkowalsky97/Human_Activities_Recognition/blob/main/SignalProcessing.ipynb)

*raw signals processing, noises filtering: 3rd order Butterworth filter, separating the gravity component: high-pass filter, windowing signals, normalisation, division of the data into training and testing components, matching the shape to the CNN model, signals visualisation*

- Example of the Data Frame with values after the signals processing:
  
<img src="https://github.com/mkowalsky97/Human_Activities_Recognition/blob/main/img/data.png" title="DATA" alt="DATA" width="500">

- Histogram with the obtained data samples:

<img src="https://github.com/mkowalsky97/Human_Activities_Recognition/blob/main/img/test_train_img.png" title="TEST/TRAIN" alt="TEST/TRAIN" width="500">


2. [CNN Model Design](https://github.com/mkowalsky97/Human_Activities_Recognition/blob/main/CNN_Model.ipynb)

*designing a CNN model, training and testing process, fitting and evaluating, parameters visualisation*

- CNN model:

<img src="https://github.com/mkowalsky97/Human_Activities_Recognition/blob/main/img/CNN_model.png" title="CNN Model" alt="CNN Model" width="500">

- Quality of the CNN model:

<img src="https://github.com/mkowalsky97/Human_Activities_Recognition/blob/main/img/params_model.png" title="Classification report" alt="Classification report" width="300">

:microscope: **MODEL ACCURACY: 95,03%**

3. [Mobile Application](https://github.com/mkowalsky97/Human_Activities_Recognition/tree/main/Application)

*the mobile application based on CNN model to clasificate 6 motion activities in real-time:*

**1. WALKING  2. WALKING UPSTAIRS  3. WALKING DOWNSTAIRS  4. SITTING  5. STANDING  6. LAYING**

<img src="https://github.com/mkowalsky97/Human_Activities_Recognition/blob/main/img/activities.png" width="530">

- Application Interface:

<div>
     <img src="https://github.com/mkowalsky97/Human_Activities_Recognition/blob/main/img/app_screen1.png" title="Interface" alt="Interface" height="350">
    <img src="https://github.com/mkowalsky97/Human_Activities_Recognition/blob/main/img/app_screen2.png" title="Inteface" alt="Interface" height="350">
</div>

:raising_hand: How to place the phone on the body:

<img src="https://github.com/mkowalsky97/Human_Activities_Recognition/blob/main/img/human_phone.jpg" height="180">

:iphone: Phone axes:

<img src="https://github.com/mkowalsky97/Human_Activities_Recognition/blob/main/img/phone_axes.png" title="Phone axes" alt="Phone axes" height="180">

<h2 align="center">
 Conclusions
</h2>

The application has been tested on a group of people aged 25, 27 and 45. The results of the above program were satisfactory. The application is very good at classifying activities with indexes 1, 3, 4 and 6, while it makes occasional errors when classifying activities with indexes 2 and 5. This model is an excellent basis for further research into creating a useful application for motion recognition.

<h2 align="center" >
  I'M GLAD FOR YOUR ATTENTION! 
   <img src="https://media.giphy.com/media/7TJXK0wFlYVeJYk3yH/giphy.gif" width="35px"/>
</h2>
