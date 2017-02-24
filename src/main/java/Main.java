import java.util.Random;

public class Main {

	static long sleep() {
		Random random = new Random();
		long time = random.nextInt(3000);
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
		return time;
	}

	public static void main(String[] args) {
		Main.A a = new Main().new A();
		int amount = 0;
		while (true) {
			a.a();
			while ((amount += sleep()) < 5000) {
				// 停个5s
			}
		}
	}

	class A {
		B b = new B();
		C c = new C();

		void a() {
			long time = sleep();
			System.out.println(String.format("sleep(%d)", time));
			if (time / 2 == 0) {
				b.b();
			} else {
				c.c();
			}
		}
	}

	class B {

		C c = new C();

		void b() {
			long time = sleep();
			System.out.println(String.format("sleep(%d)", time));
			if (time / 2 == 0) {
				c.c();
			}
		}
	}

	class C {

		void c() {
			long time = sleep();
			System.out.println(String.format("sleep(%d)", time));
		}
	}
}
