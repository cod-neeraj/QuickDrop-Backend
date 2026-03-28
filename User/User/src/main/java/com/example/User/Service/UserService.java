package com.example.User.Service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

//    @Autowired
//    private UserRepo userRepo;
//
//    @Autowired
//    private AddressRepo addressRepo;
//
//    @Autowired
//    private OtpInterface otpInterface;
//
//    @Autowired
//    private OrdersRepo ordersRepo;
//
//    @Autowired
//    private UserSearchHistoryRepo userSearchHistoryRepo;
//
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    private static final long OTP_EXPIRATION = 5; // minutes
//
//    private String buildKey(Long userId) {
//        return "otp:" + userId;
//    }
//    public void saveOtp(Long userId, String otp) {
//        String key = buildKey(userId);
//        redisTemplate.opsForValue().set(key, otp, OTP_EXPIRATION, TimeUnit.MINUTES);
//    }
//    public Users basicSignUp(UserBasicSignInRequest userBasicSignInRequest) {
//        if (userRepo.findByEmailId(userBasicSignInRequest.getEmail()).isPresent()) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        Users user = Users.builder()
//                .name(userBasicSignInRequest.getName())
//                .password(userBasicSignInRequest.getPassword())
//                .emailId(userBasicSignInRequest.getEmail())
//                .role("USER")
//                .build();
//
//        return userRepo.save(user);
//    }
//    public UserDashboard getDashBoaredData(String email) {
//        Users user = userRepo.findByEmailId(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        PageRequest page = PageRequest.of(0, 3); // Fetch only top 3
//
//        List<OrderDashBoard> recentOrders = ordersRepo.findTop3ByUserIdOrderByOrderDateDesc(user.getId(), page);
//
//        return UserDashboard.builder()
//                .name(user.getName())
//                .email(email)
//                .totalNumberOfOrders(user.getOrders().size())
//                .totalNumberOfAddress(user.getAddresses().size())
//                .totalNumberofWishListitems(user.getWishList().size())
//                .recentOrder(recentOrders)
//                .build();
//    }
//    public PersonalInfo getPersonalInfo(String email){
//      PersonalInfo personalInfo = userRepo.findPersonalInfo(email);
//      return personalInfo;
//    }
//    public PersonalInfo saveUser(String email, PersonalInfo personalInfo) {
//        Users user = userRepo.findByEmailId(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        user.setName(personalInfo.getName());
//        user.setDob(personalInfo.getDob());
//        user.setGender(personalInfo.getGender());
//
//        userRepo.save(user);
//        return userRepo.findPersonalInfo(email);
//    }
//
//    public Set<Address> getAddress(String email){
//        Users user = userRepo.findByEmailId(email)
//                .orElseThrow(()-> new RuntimeException("User not found"));
//        return  user.getAddresses();
//
//    }
//
//    public Address addAddress(AddAddressRequest addAddressRequest,String email){
//        Users user = userRepo.findByEmailId(email)
//                .orElseThrow(()-> new UsernameNotFoundException("user not found"));
//
//        Address address = Address.builder()
//                .street(addAddressRequest.getStreet())
//                .city(addAddressRequest.getCity())
//                .state(addAddressRequest.getState())
//                .country(addAddressRequest.getCountry())
//                .zipCode(addAddressRequest.getZipCode())
//                .isDefault(addAddressRequest.getIsDefault())
//                .user(user)
//                .build();
//        addressRepo.save(address);
//        user.getAddresses().add(address);
//        userRepo.save(user);
//
//        return address;
//
//    }
//    public Address updateAddress(AddAddressRequest addAddressRequest,Long id){
//        Address address = addressRepo.findById(id)
//                .orElseThrow(()-> new RuntimeException(" no address is there"));
//        address.setCity(addAddressRequest.getCity());
//        address.setState(addAddressRequest.getState());
//        address.setCountry(addAddressRequest.getCountry());
//        address.setStreet(addAddressRequest.getStreet());
//        address.setZipCode(addAddressRequest.getZipCode());
//        address.setIsDefault(addAddressRequest.getIsDefault());
//
//        addressRepo.save(address);
//        return address;
//    }
//    public boolean deleteAddress(Long id,String email) {
//        Address address = addressRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("No address found with ID: " + id));
//        Users user = userRepo.findByEmailId(email)
//                .orElseThrow(() -> new RuntimeException("No user found with ID: " + id));
//
//        user.getAddresses().removeIf(a -> a.getId().equals(address.getId()));
//        userRepo.save(user);
//
//        addressRepo.delete(address);
//
//
//
//        return !addressRepo.existsById(id);
//    }
//
//    public List<Search_History> searchHistory(Long id){
//        Optional<List<UserSearchHistory>> userSearchHistory = userSearchHistoryRepo.findByUserId(id);
//        List<Search_History> list = new ArrayList<>();
//        if(userSearchHistory.isPresent()) {
//            for (UserSearchHistory userSearchHistory1 : userSearchHistory.get()) {
//                Search_History searchHistory = Search_History.builder()
//                        .id(userSearchHistory1.getId())
//                        .text(userSearchHistory1.getQuery())
//                        .build();
//                list.add(searchHistory);
//
//
//            }
//        }
//        return list;
//
//
//    }
//
//
//    public boolean getOtp(Long userId){
//        Users user = userRepo.findById(userId).orElseThrow(()-> new RuntimeException("use not found"));
//        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
//        saveOtp(userId, otp);
//        SendEmailDTO sendEmailDTO = SendEmailDTO.builder()
//                .otp(otp)
//                .email(user.getEmailId()).build();
//       ResponseEntity<?> response = otpInterface.sendEmail(sendEmailDTO);
//     if(response.getStatusCode().is2xxSuccessful()){
//      return true;
//  }
//  return false;
//    }
//
//    public boolean validateOtp(Long userId, String otp) {
//        String key = buildKey(userId);
//        String savedOtp = redisTemplate.opsForValue().get(key).toString();
//
//        if (savedOtp == null) {
//            return false;
//        }
//
//        if (savedOtp.equals(otp)) {
//            redisTemplate.delete(key);
//            return true;
//        }
//        return false;
//    }
//
//
//    public boolean changePassword(String password,Long userId){
//        Users user = userRepo.findById(userId).orElseThrow(()-> new RuntimeException(" not found"));
//        user.setPassword(password);
//        return true;
//    }
//    //    @Cacheable(value = "Users", key = "#id")
//    @Transactional
//    public Optional<Users> getUser(String id){
//       return userRepo.findByEmailId(id);
//
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Users user = userRepo.findByEmailId(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getEmailId(),
//                user.getPassword(),
//                List.of(new SimpleGrantedAuthority(user.getRole()))
//        );
//
//    }

//
}