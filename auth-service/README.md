## Auth Service

Entity for user

After setup dockerfile and docker compose, don't forget the application properties

## Debugging Database not shown 

- Volume baru, ada dua database terpisah tapi sama-sama dalam satu connection

- Port yang dispecified beda, connection selalu mengikuti host port (5001:5432 => 5001).


### Notes
- Why if only one properties, use constructor ? If more, use getter and setter?

    - Construtor easier because no need to init new object, assign to setter variable, more code on return value

- Controller : 
    - Conversion of request body into DTO, and passed as argument to function 


### Chaining

```java

Optional<User> user = userService
.findByEmail(loginRequestDTO.getEmail())

// chaining result from previous call
.filter(u -> passwordEncoder.matches(loginRequestDTO.getPassword(), u.getPassword())); 

//chaining result from previous call, again
.map(u -> jwtUtil.generateToken(u.getEmail(), u.getRole()));  

```

### Debugging
Response 401 with WWW-Authenticate: Basic realm="Realm"

That means cannot reach the request. 
Solve :
- Rebuild the project
- Put right .env configuration on docker-compose

## JWT
token that imported from JWT can be decoded and gives us payload data, 

with right algorithm, ofc

## Validation
Use API Gateway, need a mechanism to validate token with auth services, so user can be authenticated with right authorization
