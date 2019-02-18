package mmp.im.common.protocol;


import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;

public class Test {
    public static void main(String[] args) {
        // 序列化
        AddressBookProtos.Person.Builder person = AddressBookProtos.Person.newBuilder();
        person.setEmail("xxxxxxxx@qq.com").setId(1).setName("mmp");

        AddressBookProtos.Person.PhoneNumber.Builder number = AddressBookProtos.Person.PhoneNumber.newBuilder();
        AddressBookProtos.Person.PhoneType type = AddressBookProtos.Person.PhoneType.HOME;
        person.setType(type);
        person.getType().getNumber();
        number.setNumber("XXXXXXXX");
        person.addPhones(number);

        AddressBookProtos.Person.PhoneNumber phoneNumber = AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("ds").build();
        AddressBookProtos.Person.PhoneNumber phoneNumber2 = AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("ds2").build();


        // 包装
        person.setData(Any.pack(phoneNumber));

        // System.out.println(person.getData().getTypeUrl());

        AddressBookProtos.Person b = person.build();
        AddressBookProtos.Person c = person.setData(Any.pack(phoneNumber2)).build();

        try {
            AddressBookProtos.Person.PhoneNumber p = b.getData().unpack(AddressBookProtos.Person.PhoneNumber.class);
            AddressBookProtos.Person.PhoneNumber e = c.getData().unpack(AddressBookProtos.Person.PhoneNumber.class);
            // System.out.println(p.getNumber());
            System.out.println(b.getClass());
            System.out.println(AddressBookProtos.Person.getDescriptor());
            System.out.println(AddressBookProtos.Person.getDefaultInstance().getClass());
            System.out.println("type====");

        } catch (Exception e) {
            e.printStackTrace();
        }

        // person.putProtoMap("k", "v");
        //
        // AddressBookProtos.AddressBook.Builder address = AddressBookProtos.AddressBook.newBuilder();
        // address.addPeople(person);
        // address.addPeople(person);
        //
        // AddressBookProtos.AddressBook book = address.build();
        // byte[] result = book.toByteArray(); // 序列化
        //
        // // 反序列化
        // AddressBookProtos.AddressBook read;
        // try {
        //     read = AddressBookProtos.AddressBook.parseFrom(result);
        //     // 转成中文
        //     System.out.println(read.getPeopleList().get(0).getNameBytes().toStringUtf8());
        //     System.out.println(read);
        // } catch (InvalidProtocolBufferException e) {
        //     e.printStackTrace();
        // }
    }

}

 /*
        people{
            name:"\345\274\240\344\270\211"
            id:1
            email:"xxxxxxxx@qq.com"
            phones{
                number:"XXXXXXXX"
            }
        }
        people{
            name:"\345\274\240\344\270\211"
            id:1
            email:"xxxxxxxx@qq.com"
            phones{
                number:"XXXXXXXX"
            }
        }
  */
