package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;


/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        Inventory inventory = Inventory.getInstance();
        ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
        TimeService timeService;
        MoneyRegister moneyRegister = MoneyRegister.getInstance();
        HashMap<Integer, Customer> customersToOutput = new HashMap<>();
        LinkedList<MicroService> services = new LinkedList<>();
        //args
        String configurationFile = args[0];
        String outputCustomer = args[1];
        String outputBooks = args[2];
        String outputReceipts = args[3];
        String outputMoneyRegister = args[4];


        LinkedList<Thread> threads = new LinkedList<>();
        Gson gson = new Gson();
        try {

            JsonObject jsonObject = gson.fromJson(new FileReader(configurationFile), JsonObject.class);
            //Add the books to inventory
            BookInventoryInfo[] books = gson.fromJson(jsonObject.get("initialInventory").getAsJsonArray(), BookInventoryInfo[].class);
            inventory.load(books);

            //add vehicles to resource holder
            JsonObject sources2 = gson.fromJson(jsonObject.get("initialResources").getAsJsonArray().get(0), JsonObject.class);
            DeliveryVehicle[] vehicles = gson.fromJson((sources2.get("vehicles")), DeliveryVehicle[].class);
            resourcesHolder.load(vehicles);


            //count how much services we have so we can do countdown
            int numOfServices = gson.fromJson(jsonObject.get("services").getAsJsonObject().get("selling"), int.class);
            numOfServices += gson.fromJson(jsonObject.get("services").getAsJsonObject().get("inventoryService"), int.class);
            numOfServices += gson.fromJson(jsonObject.get("services").getAsJsonObject().get("logistics"), int.class);
            numOfServices += gson.fromJson(jsonObject.get("services").getAsJsonObject().get("resourcesService"), int.class);
            JsonArray numOcustomers = gson.fromJson(jsonObject.get("services").getAsJsonObject().get("customers"), JsonArray.class);
            numOfServices += numOcustomers.size();
            final CountDownLatch countDownLatch = new CountDownLatch(numOfServices);


            //add sellingServices
            int sellingServices = gson.fromJson(jsonObject.get("services").getAsJsonObject().get("selling"), int.class);
            for (int i = 0; i < sellingServices; i++) {

                services.addLast(new SellingService(i, countDownLatch));
                threads.add(new Thread(services.getLast()));

            }
            //add inventoryServices
            int inventoryService = gson.fromJson(jsonObject.get("services").getAsJsonObject().get("inventoryService"), int.class);
            for (int i = 0; i < inventoryService; i++) {
                services.addLast(new InventoryService(i, countDownLatch));
                threads.add(new Thread(services.getLast()));

            }
            //add logisticsServices
            int logistics = gson.fromJson(jsonObject.get("services").getAsJsonObject().get("logistics"), int.class);
            for (int i = 0; i < logistics; i++) {
                services.addLast(new LogisticsService(i, countDownLatch));
                threads.add(new Thread(services.getLast()));
            }
            //add ResourceService
            int resourceService = gson.fromJson(jsonObject.get("services").getAsJsonObject().get("resourcesService"), int.class);
            for (int i = 0; i < resourceService; i++) {
                services.addLast(new ResourceService(i, countDownLatch));

                threads.add(new Thread(services.getLast()));
            }

            //add API Services
            JsonArray customers = gson.fromJson(jsonObject.get("services").getAsJsonObject().get("customers"), JsonArray.class);
            for (int i = 0; i < customers.size(); i++) {
                int id = gson.fromJson(customers.get(i).getAsJsonObject().get("id"), int.class);
                String name = gson.fromJson(customers.get(i).getAsJsonObject().get("name"), String.class);
                String address = gson.fromJson(customers.get(i).getAsJsonObject().get("address"), String.class);
                int distance = gson.fromJson(customers.get(i).getAsJsonObject().get("distance"), int.class);
                int creditnum = gson.fromJson(customers.get(i).getAsJsonObject().get("creditCard").getAsJsonObject().get("number"), int.class);
                int creditamount = gson.fromJson(customers.get(i).getAsJsonObject().get("creditCard").getAsJsonObject().get("amount"), int.class);
                JsonArray orderSchedule = gson.fromJson(customers.get(i).getAsJsonObject().get("orderSchedule"), JsonArray.class);
                ConcurrentSkipListMap<Integer, LinkedList<String>> scheduleToAdd = new ConcurrentSkipListMap<>();
                for (JsonElement jsonObject1 : orderSchedule
                ) {
                    int tick = gson.fromJson(jsonObject1.getAsJsonObject().get("tick"), int.class);
                    String bookTitle = gson.fromJson(jsonObject1.getAsJsonObject().get("bookTitle"), String.class);

                    if (scheduleToAdd.containsKey(tick)) {
                        LinkedList<String> linkedList = scheduleToAdd.get(tick);
                        linkedList.add(bookTitle);
                        scheduleToAdd.put(tick, linkedList);
                    } else {
                        LinkedList<String> linkedList = new LinkedList<>();
                        linkedList.add(bookTitle);
                        scheduleToAdd.put(tick, linkedList);

                    }
                }

                Customer customerToAddd = new Customer(id, name, address, distance, creditnum, creditamount);
                services.addLast(new APIService(i, customerToAddd, scheduleToAdd, countDownLatch));
                threads.add(new Thread(services.getLast()));
                customersToOutput.put(customerToAddd.getId(), customerToAddd);
            }

            //update Time Service
            int speed = gson.fromJson(jsonObject.get("services").getAsJsonObject().get("time").getAsJsonObject().get("speed"), int.class);
            int duration = gson.fromJson(jsonObject.get("services").getAsJsonObject().get("time").getAsJsonObject().get("duration"), int.class);

            timeService = new TimeService(speed, duration);
            services.addLast(timeService);

            for (Thread thread : threads) {
                thread.start();

            }
            threads.addLast(new Thread(timeService));
            Thread timeServiceThread = threads.getLast();
            try {
                countDownLatch.await();
                timeServiceThread.start();


            } catch (InterruptedException e) {
                timeServiceThread.start();
            }



            for (Thread thread : threads) {
                try {

                    thread.join();


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            //printing output
            inventory.printInventoryToFile(outputBooks); //books
            moneyRegister.printOrderReceipts(outputReceipts); //reciepts


            //print MoneyRegister
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(outputMoneyRegister);
                ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
                oos.writeObject(moneyRegister);
                oos.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            //print customers
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(outputCustomer);
                ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
                oos.writeObject(customersToOutput);
                oos.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

}
