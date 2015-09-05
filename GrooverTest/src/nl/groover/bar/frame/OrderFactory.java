package nl.groover.bar.frame;

import android.database.Cursor;

//Constructor class to make an order
public class OrderFactory {

	public static Order createEmptyOrder(Customer customer, Cursor cursor) {

		cursor.moveToFirst();
		OrderUnit[] units = new OrderUnit[cursor.getCount()];
		int i = 0;
		OrderUnit u;
		Article a;

		while (cursor.getPosition() < cursor.getCount()) {

			a = new Article(cursor.getInt(0), cursor.getDouble(2),
					cursor.getString(1), cursor.getInt(3)<1, cursor.getInt(4));
			u = new OrderUnit(a, 0);
			units[i] = u;
			cursor.moveToNext();
			i++;
		}

		return new Order(customer, units);
	}

	public static Order createExistingOrder(Customer customer,
			Cursor c_Articles, Cursor order) {

		order.moveToFirst();
		c_Articles.moveToFirst();
		OrderUnit[] units = new OrderUnit[c_Articles.getCount()];
		OrderUnit u;
		Article a;

		while (c_Articles.getPosition() < c_Articles.getCount()) {
			a = new Article(c_Articles.getInt(0), c_Articles.getDouble(2),
					c_Articles.getString(1), c_Articles.getInt(3)<1, c_Articles.getInt(4));
			u = new OrderUnit(a, 0);
			units[c_Articles.getPosition()] = u;
			c_Articles.moveToNext();
		}

		while (order.getPosition() < order.getCount()) {

			for (int j = 0; j < units.length; j++) {
				if (units[j].getArticle().getId() == order.getInt(1)) {
					units[j].setAmount(order.getInt(2));
					units[j].getArticle().setName(order.getString(3));
					units[j].getArticle().setPrice(order.getDouble(4));
				}
			}
			order.moveToNext();
		}
		return new Order(customer, units);
	}
}
