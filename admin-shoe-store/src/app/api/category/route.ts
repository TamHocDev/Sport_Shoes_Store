import { NextResponse } from "next/server";
import { db } from "@/lib/firebaseAdmin";

// Lấy tham chiếu đến node "Category"
const categoryRef = db.ref("Category");

export async function GET() {
  try {
    // Lấy dữ liệu từ Realtime Database
    const snapshot = await categoryRef.once("value");
    const categories = snapshot.val();
    console.log("Fetched categories:", categories); // Log the category data
    return NextResponse.json(categories ? Object.values(categories) : []);
  } catch (error) {
    console.error("Lỗi khi fetch categories:", error);
    return NextResponse.json({ error: "Failed to fetch categories" }, { status: 500 });
  }
}

export async function POST(req: Request) {
  try {
    const body = await req.json();
    // Tạo một ID mới cho category
    const newCategoryRef = categoryRef.push();
    await newCategoryRef.set(body);
    return NextResponse.json({ id: newCategoryRef.key, ...body });
  } catch (error) {
    console.error("Lỗi khi thêm category:", error); // Log lỗi
    return NextResponse.json({ error: "Failed to add category" }, { status: 500 });
  }
}

export async function PUT(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const id = searchParams.get("id");
    if (!id) return NextResponse.json({ error: "Missing ID" }, { status: 400 });

    const body = await req.json();
    // Cập nhật category với ID cụ thể
    await categoryRef.child(id).update(body);
    return NextResponse.json({ id, ...body });
  } catch (error) {
    console.error("Lỗi khi cập nhật category:", error); // Log lỗi
    return NextResponse.json({ error: "Failed to update category" }, { status: 500 });
  }
}

export async function DELETE(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const id = searchParams.get("id");
    if (!id) return NextResponse.json({ error: "Missing ID" }, { status: 400 });

    // Xóa category với ID cụ thể
    await categoryRef.child(id).remove();
    return NextResponse.json({ success: true });
  } catch (error) {
    console.error("Lỗi khi xóa category:", error); // Log lỗi
    return NextResponse.json({ error: "Failed to delete category" }, { status: 500 });
  }
}
