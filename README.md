# AnuradhaKumbhar

Submit code  # BaggageSystem
/*
inputs used for testing

#conveyor_system
Concourse_A_Ticketing A5 5
A5 BaggageClaim 5
A5 A10 4
A5 A1 6
A1 A2 1
A2 A3 1
A3 A4 1
A10 A9 1
A9 A8 1
A8 A7 1
A7 A6 1
#departure_list
UA10 A1 MIA 08:00
UA11 A1 LAX 09:00
UA12 A1 JFK 09:45
UA13 A2 JFK 08:30
UA14 A2 JFK 09:45
UA15 A2 JFK 10:00
UA16 A3 JFK 09:00
UA17 A4 MHT 09:15
UA18 A5 LAX 10:15
#bags_list
0001 Concourse_A_Ticketing UA12
0002 A5 UA17
0003 A2 UA10
0004 A8 UA18
0005 A7 ARRIVAL
0006 A7 INVALID_FLIGHT_ID
0006 A2 UA13


output
----------------------------------------------------
..............................................................
bag no : Entry Gate : Flight ID=0001:Concourse_A_Ticketing:UA12
Entry Point:Destination==Concourse_A_Ticketing::A1
Final output for Bag=0001 [Concourse_A_Ticketing, A5, A1] 11.0
..............................................................
bag no : Entry Gate : Flight ID=0002:A5:UA17
Entry Point:Destination==A5::A4
Final output for Bag=0002 [A5, A1, A2, A3, A4] 9.0
..............................................................
bag no : Entry Gate : Flight ID=0003:A2:UA10
Entry Point:Destination==A2::A1
Final output for Bag=0003 [A2, A1] 1.0
..............................................................
bag no : Entry Gate : Flight ID=0004:A8:UA18
Entry Point:Destination==A8::A5
Final output for Bag=0004 [A8, A9, A10, A5] 6.0
..............................................................
bag no : Entry Gate : Flight ID=0005:A7:ARRIVAL
Entry Point:Destination==A7::BaggageClaim
Final output for Bag=0005 [A7, A8, A9, A10, A5, BaggageClaim] 12.0
..............................................................
bag no : Entry Gate : Flight ID=0006:A7:INVALID_FLIGHT_ID
Flight ID Specified in Bags Details not Found in Departure list...
..............................................................
bag no : Entry Gate : Flight ID=0006:A2:UA13
Entry Point:Destination==A2::A2
Final output for Bag=0006 [A2] 0.0

/*
