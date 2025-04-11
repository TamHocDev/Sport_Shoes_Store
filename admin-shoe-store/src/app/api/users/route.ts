import { NextResponse } from "next/server";
import { db } from "@/lib/firebaseAdmin";

// Tham chiếu đến node "users" trong Realtime Database
const usersRef = db.ref("users");

// Lấy danh sách tất cả người dùng
export async function GET() {
  try {
    const snapshot = await usersRef.once("value");
    const users = snapshot.val();
    return NextResponse.json(users ? Object.values(users) : []);
  } catch (error) {
    console.error("Lỗi khi lấy danh sách người dùng:", error);
    return NextResponse.json({ error: "Không thể lấy danh sách người dùng" }, { status: 500 });
  }
}

// Thêm một người dùng mới
export async function POST(req: Request) {
  try {
    const body = await req.json();
    if (!body.userId || !body.name || !body.email || !body.phoneNumber) {
      return NextResponse.json({ error: "Thiếu thông tin bắt buộc" }, { status: 400 });
    }
    await usersRef.child(body.userId).set(body);
    return NextResponse.json({ id: body.userId, ...body });
  } catch (error) {
    console.error("Lỗi khi thêm người dùng:", error);
    return NextResponse.json({ error: "Không thể thêm người dùng" }, { status: 500 });
  }
}

// Cập nhật thông tin người dùng
export async function PUT(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const userId = searchParams.get("id");
    if (!userId) return NextResponse.json({ error: "Thiếu ID người dùng" }, { status: 400 });

    const body = await req.json();
    await usersRef.child(userId).update(body);
    return NextResponse.json({ id: userId, ...body });
  } catch (error) {
    console.error("Lỗi khi cập nhật người dùng:", error);
    return NextResponse.json({ error: "Không thể cập nhật người dùng" }, { status: 500 });
  }
}

// Xóa người dùng
export async function DELETE(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const userId = searchParams.get("id");
    if (!userId) return NextResponse.json({ error: "Thiếu ID người dùng" }, { status: 400 });

    await usersRef.child(userId).remove();
    return NextResponse.json({ success: true });
  } catch (error) {
    console.error("Lỗi khi xóa người dùng:", error);
    return NextResponse.json({ error: "Không thể xóa người dùng" }, { status: 500 });
  }
}
