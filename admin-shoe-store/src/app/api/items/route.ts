import { NextResponse } from "next/server";
import { db } from "@/lib/firebaseAdmin";

// Ottieni un riferimento al nodo "Items"
const itemsRef = db.ref("Items");

export async function GET() {
  try {
    // Ottieni i dati da Realtime Database
    const snapshot = await itemsRef.once("value");
    const items = snapshot.val();
    console.log("Articoli recuperati:", items); // Registra i dati dell'articolo
    return NextResponse.json(items ? Object.values(items) : []);
  } catch (error) {
    console.error("Errore durante il recupero degli articoli:", error);
    return NextResponse.json({ error: "Impossibile recuperare gli articoli" }, { status: 500 });
  }
}

export async function POST(req: Request) {
  try {
    const body = await req.json();
    // Crea un nuovo ID per l'articolo
    const newItemRef = itemsRef.push();
    await newItemRef.set(body);
    return NextResponse.json({ id: newItemRef.key, ...body });
  } catch (error) {
    console.error("Errore durante l'aggiunta dell'articolo:", error); // Registra l'errore
    return NextResponse.json({ error: "Impossibile aggiungere l'articolo" }, { status: 500 });
  }
}

export async function PUT(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const id = searchParams.get("id");
    if (!id) return NextResponse.json({ error: "ID mancante" }, { status: 400 });

    const body = await req.json();
    // Aggiorna l'articolo con l'ID specificato
    await itemsRef.child(id).update(body);
    return NextResponse.json({ id, ...body });
  } catch (error) {
    console.error("Errore durante l'aggiornamento dell'articolo:", error); // Registra l'errore
    return NextResponse.json({ error: "Impossibile aggiornare l'articolo" }, { status: 500 });
  }
}

export async function DELETE(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const id = searchParams.get("id");
    if (!id) return NextResponse.json({ error: "ID mancante" }, { status: 400 });

    // Elimina l'articolo con l'ID specificato
    await itemsRef.child(id).remove();
    return NextResponse.json({ success: true });
  } catch (error) {
    console.error("Errore durante l'eliminazione dell'articolo:", error); // Registra l'errore
    return NextResponse.json({ error: "Impossibile eliminare l'articolo" }, { status: 500 });
  }
}