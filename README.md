# DNS-Client
This project is for ECSE 316 class

## Calling Syntax

Compile:

```bash
Javac DnsClient.java
```

Run: 

```bash
Java DnsClient [-t timeout] [-r max-retries] [-p port] [-mx|-ns] @server name
```

### Argument Definitions

* `timeout`(optional): gives how long to wait, in seconds, before retransmitting an unanswered query. Default value: 5.
* `max-retries`(optional): is the maximum number of times to retransmit an unanswered query before giving up. Default value: 3
* `port`(optional): is the UDP port number of the DNS server. Default value: 53.
* `-mx` or `-ns` flags (optional): indicate whether to send a MX (mail server) or NS (name server) query. At most one of these can be given, and if neither is given then the client should send a type A (IP address) query
* `server` (required): is the IPv4 address of the DNS server, in a.b.c.d.format 
  * @132.206.85.18 for McGill 
  * @8.8.8.8 for Google
* `name` (required): is the domain name to query for.


### Examples:

* Requesting IP Address of www.mcgill.ca from McGill DNS server:

``` bash 
java DnsClient @132.206.85.18 www.mcgill.ca
```

* Requesting mcgill.ca mail sever from Google's public DNS with timeout of 10 seconds and at most 2 entries:

``` bash
java DnsClient –t 10 –r 2 –mx @8.8.8.8 mcgill.ca
```

## Output behavior

### Displaying Request
* DnsClient sending request for [name] 
* Server: [server IP address]
* Request type: [A | MX | NS]

### Displaying Time Passed To Receive the Response
* Response received after [time] seconds ([num-retries] retries)

## Displaying Response
* ***Answer Section ([num-answers] records)***
* IP  [ip address] [seconds can cache] [auth | nonauth]
* CNAME  [alias]  [seconds can cache]  [auth | nonauth]
* MX [alias] [pref] [seconds can cache] [auth | nonauth] 
* NS [alias] [seconds can cache]  [auth | nonauth]



### Input: IP Address Record
``` bash 
java DnsClient @8.8.8.8 www.fb.com
```

### Output 
```
DnsClient sending request for www.fb.com
Server: 8.8.8.8
Request type: A
Response received after 0.024 seconds (0 retries)

***Answer Section (3 answerRecords)***
CNAME	www.facebook.com	6612	nonauth
CNAME	star-mini.c10r.facebook.com	3305	nonauth
IP	31.13.80.36	59	nonauth
```

### Input: Name Server Record
``` bash 
java DnsClient -nx @8.8.8.8 www.fb.com
```

### Output 
``` 
DnsClient sending request for www.fb.com
Server: 8.8.8.8
Request type: A
Response received after 0.065 seconds (0 retries)

***Answer Section (3 answerRecords)***
CNAME	www.facebook.com	7199	nonauth
CNAME	star-mini.c10r.facebook.com	3023	nonauth
IP	31.13.80.36	59	nonauth
```

### Input: Mail Server Record
``` bash 
java DnsClient -mx @8.8.8.8 yahoo.com
```

### Output 
```
DnsClient sending request for yahoo.com
Server: 8.8.8.8
Request type: MX
Response received after 0.009 seconds (0 retries)

***Answer Section (3 answerRecords)***
MX	mta7.am0.yahoodns.net	1	804	nonauth
MX	mta5.am0.yahoodns.net	1	804	nonauth
MX	mta6.am0.yahoodns.net	1	804	nonauth
```
