import { NextResponse } from "next/server";
import { db } from "@/lib/firebaseAdmin";

const bannerRef = db.ref("Banner");

export async function GET() {
    try {
      const snapshot = await bannerRef.once("value");
      const banners = snapshot.val();
      console.log("Fetched banners:", banners); // Log the banners data
      return NextResponse.json(banners ? Object.values(banners) : []);
    } catch (error) {
      console.error("Lỗi khi fetch banners:", error);
      return NextResponse.json({ error: "Failed to fetch banners" }, { status: 500 });
    }
  }
  

export async function POST(req: Request) {
  try {
    const body = await req.json();
    const newBannerRef = bannerRef.push();
    await newBannerRef.set(body);
    return NextResponse.json({ id: newBannerRef.key, ...body });
  } catch (error) {
    console.error("Lỗi khi thêm banner:", error); // Log lỗi
    return NextResponse.json({ error: "Failed to add banner" }, { status: 500 });
  }
}

export async function PUT(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const id = searchParams.get("id");
    if (!id) return NextResponse.json({ error: "Missing ID" }, { status: 400 });

    const body = await req.json();
    await bannerRef.child(id).update(body);
    return NextResponse.json({ id, ...body });
  } catch (error) {
    console.error("Lỗi khi cập nhật banner:", error); // Log lỗi
    return NextResponse.json({ error: "Failed to update banner" }, { status: 500 });
  }
}

export async function DELETE(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const id = searchParams.get("id");
    if (!id) return NextResponse.json({ error: "Missing ID" }, { status: 400 });

    await bannerRef.child(id).remove();
    return NextResponse.json({ success: true });
  } catch (error) {
    console.error("Lỗi khi xóa banner:", error); // Log lỗi
    return NextResponse.json({ error: "Failed to delete banner" }, { status: 500 });
  }
}
