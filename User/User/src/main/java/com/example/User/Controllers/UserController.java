package com.example.User.Controllers;

//
//@RestController
//@RequestMapping("/user")
public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @PostMapping("/basicSignup")
//    public ResponseEntity<?> basicSignUp(@RequestBody @Valid UserBasicSignInRequest userBasicSignInRequest) {
//        try {
//            userBasicSignInRequest.setPassword(passwordEncoder.encode(userBasicSignInRequest.getPassword()));
//            Users user = userService.basicSignUp(userBasicSignInRequest);
//            if (user != null) {
//                ApiResponse<String> apiResponse = ApiResponse.<String>builder()
//                        .success(true)
//                        .message("User signed successfully")
//                        .data(null)
//                        .build();
//                return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
//            }
//            ApiResponse<String> apiResponse = ApiResponse.<String>builder()
//                    .success(false)
//                    .message("User signUp failed")
//                    .data(null)
//                    .build();
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
//        } catch (Exception e) {
//            ApiResponse<String> apiResponse = ApiResponse.<String>builder()
//                    .success(false)
//                    .message("User Already exist")
//                    .data(null)
//                    .build();
//            return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
//        }
//    }
//
//
//    @GetMapping("/men")
//    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
//        if (authentication != null && authentication.isAuthenticated()) {
//            return ResponseEntity.ok(authentication.getPrincipal());
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(@RequestBody @Valid LoginUserRequest loginUserRequest,
//                                       HttpServletResponse response) {
//
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginUserRequest.getEmail(), loginUserRequest.getPassword()));
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String token = jwtUtil.generateToken(userDetails);
//        Cookie cookie = new Cookie("jwt", token);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(false);
//        cookie.setPath("/");
//        cookie.setMaxAge(10 * 60 * 60);
//        response.addCookie(cookie);
//
//        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
//                .success(true)
//                .message("Login Success")
//                .data(null)
//                .build();
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
//
//    }
//
//
//    @GetMapping("/me")
//    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie c : cookies) {
//                if ("jwt".equals(c.getName())) {
//                    String token = c.getValue();
//                    String email = jwtUtil.extractUsername(token);
//                    if(email != null){
//                        return ResponseEntity.ok(true);
//                    }
//                }
//            }
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
//    }
//
//    @GetMapping("/getAccess")
//    public ResponseEntity<?> getAccess(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            System.out.println(userDetails);
//            return ResponseEntity.ok(Map.of(
//                    "email", userDetails.getUsername()
//            ));
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//        }
//    }
//
//    @GetMapping("/forgotPassword/{userId}")
//    public ResponseEntity<?> getOtp(@PathVariable Long userId){
//        boolean bool = userService.getOtp(userId);
//        if(bool){
//            return ResponseEntity.ok("sent");
//        }else{
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("not sent");
//        }
//
//
//    }
//
//    @PostMapping("/verify-OTP/{otp}/{userId}")
//    public ResponseEntity<?> verifyOtp(@PathVariable String otp, @PathVariable Long userId){
//        Boolean bool = userService.validateOtp(userId,otp);
//        if(bool){
//            return ResponseEntity.ok(true);
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
//
//    }
//
//    @PostMapping("/changePassword/{password}/{userId}")
//    public ResponseEntity<?> changPassword(@PathVariable String password,@PathVariable Long userId){
//        Boolean bool = userService.changePassword(password,userId);
//        return ResponseEntity.ok(true);
//    }
//
//    @GetMapping("/getUser/{id}")
//    public ResponseEntity<?> getUser(@PathVariable String id) {
//        Users user = userService.getUser(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
//
//        return ResponseEntity.ok(user);
//    }
//
//    @GetMapping("/getDashBoardData")
//    public ResponseEntity<?> getDashboaredData() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null &&
//                authentication.isAuthenticated() &&
//                authentication.getPrincipal() instanceof UserDetails) {
//
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String email = userDetails.getUsername();
//            UserDashboard userDashboard = userService.getDashBoaredData(email);
//            return ResponseEntity.ok(userDashboard);
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
//    }
//
//
//    @GetMapping("/getPersonalInfo")
//    public ResponseEntity<?> getPersonalInfo(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String email = userDetails.getUsername();  // No need to cast getUsername() again
//            PersonalInfo personalInfo = userService.getPersonalInfo(email);
//
//            if (personalInfo != null) {
//                return ResponseEntity.ok(Map.of("data", personalInfo));
//            }
//
//            // Send JSON response with error
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("error", "Personal info not found"));
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "Unauthorized"));
//        }
//
//
//
//    }
//
//    @PostMapping("/saveUser")
//    public ResponseEntity<?> saveUser(@RequestBody @Valid PersonalInfo personalInfo){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String email = userDetails.getUsername();  // No need to cast getUsername() again
//           PersonalInfo personalInfo1  = userService.saveUser(email, personalInfo);
//
//            if (personalInfo1 != null) {
//                return ResponseEntity.ok(Map.of("data", personalInfo1));
//            }
//
//            // Send JSON response with error
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("error", "Personal info not found"));
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "Unauthorized"));
//        }
//
//    }
//
//    @GetMapping("/getAddressList")
//    public ResponseEntity<?> getAddress() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.isAuthenticated()) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String email = userDetails.getUsername();
//            Set<Address> addresses = userService.getAddress(email);
//
//            return ResponseEntity.ok(Map.of("data", addresses)); // ✅ always return 200
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "Unauthorized"));
//        }
//    }
//
//    @PostMapping("/saveAddress")
//    public ResponseEntity<?> saveAddress(@RequestBody @Valid AddAddressRequest addAddressRequest){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String email = userDetails.getUsername();
//            Address address= userService.addAddress(addAddressRequest,email);
//
//            if (address != null) {
//                return ResponseEntity.ok(Map.of("data", address));
//            }
//
//            // Send JSON response with error
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("error", "Personal info not found"));
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "Unauthorized"));
//        }
//    }
//
//    @PutMapping("/updateAddress/{id}")
//    public ResponseEntity<?> updateAddress(@RequestBody @Valid AddAddressRequest addAddressRequest, @PathVariable Long id){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String email = userDetails.getUsername();
//            Address address= userService.updateAddress(addAddressRequest,id);
//
//            if (address != null) {
//                return ResponseEntity.ok(Map.of("data", address));
//            }
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("error", "Personal info not found"));
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "Unauthorized"));
//        }
//    }
//
//    @DeleteMapping("/deleteAddress/{id}")
//    public ResponseEntity<?> deleteAddress(@PathVariable Long id){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String email = userDetails.getUsername();
//            boolean bool = userService.deleteAddress(id,email);
//
//            if (bool) {
//                return ResponseEntity.ok(Map.of("message",true ));
//            }
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("error", "Personal info not found"));
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("error", "Unauthorized"));
//        }
//    }
//
//    @GetMapping("/search-history/{id}")
//    public ResponseEntity<?> getSearch_History_Products(@PathVariable Long id){
//        List<Search_History> searchHistory = userService.searchHistory(id);
//        if(searchHistory.isEmpty()){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no search yet");
//        }
//        return ResponseEntity.ok(searchHistory);
//
//    }

}