# Sydney Java Meetup #10
https://www.meetup.com/sydney-java/events/286477523/

### Program
6pm - Networking/Drinks/Food.  
6:30pm - Event Intro and Job Shouts.  
6:35pm - Miguel Yap (Pet Circle) speaking about utilising Java to build cloud functions on GCP.  
7:00pm - break.  
7.15pm - Exploring Quarkus + MongoDB (Live-Coding)  
8pm Event Finish.  

### Intro
Program

Quick acknowledgement of:
* first meetup since pandemic (Jan 29, 2020!)
* changes for me since the last meetup
* changes in the world since the last meetup - upheaval, downturns, uncertainty

Ethos, vibe
* openness
* inclusiveness
* no experts, we're a community of peers rather than a mixture of experts and novices
* supportiveness, we're here for each other

### Job Shouts
“Before we get into the tech talks, are there any job shoutouts?”

<Miguel Yap - GCP cloud functions>

### Quarkus
Why Quarkus?  
<img width="773" alt="image" src="https://user-images.githubusercontent.com/1756555/192748496-47a03ce5-52ab-4f5b-9a8a-d0994ba266a9.png">
https://www.redhat.com/files/resources/cm-quarkus-supersonic-subatomic-java-infographic-f19144-201908-en.pdf
https://quarkus.io/vision/container-first


Quarkus is a highly opinionated framework, whose main aim in life is to make Java apps that are lean, mean and can mix it with Go and other native compiled languages when it comes to building microservices in the sense of small footprint lightweight purpose-built applications.

Because of this purpose to have a minimal memory and bundle size footprint with fast startup times for instant autoscaling, they have made some quite interesting design choices that limit flexibility to achieve this performance outcome.

This also leads to quite a steep learning curve. I feel like I’ve been falling in and out of love with Quarkus as I navigate the slopes of this learning curve. 
<img width="1218" alt="image" src="https://user-images.githubusercontent.com/1756555/192747590-06570ca7-4550-4872-9912-7198f6710421.png">


When wouldn’t you use Quarkus? I wouldn’t recommend taking an existing monolithic Java application and porting it to Quarkus. It’s very likely you are already using libraries and approaches that would not be compatible with their build-time optimisations and you could get quagmired in migration hassles. 

When would you? When you want to take that same monolith and rebuild it using true microservices (maybe with a strangler pattern). Each micro service built with Quarkus would take advantage of these optimisations and be implemented using the approaches they recommend for high performance high density containerised deployments.

##### Questions
About questions - please stop me if you have a question and we can dive into it. I will try to keep moving through the example though, so if you’re really stuck or want to work through something in detail let me know afterwards and I’m happy to stick around and work through it, or also feel free to get in touch with me after the meetup. 

#### Live Coding!

Quick demo of the (impossible in 50 minute!) goal: http://petrace.mycodefu.com:8080/

Start a new repo, copy the issues from this one and have at it.
