"use client";

import { useEffect, useState } from "react";
import Image from "next/image";
import { StarIcon } from "@heroicons/react/20/solid";
import { PencilIcon, TrashIcon, ChevronUpIcon, ChevronDownIcon } from "@heroicons/react/24/outline";

interface Item {
  id?: string;
  category: string;
  description: string;
  offPercent: string;
  oldPrice: number;
  picUrl: string[];
  price: number;
  rating: number;
  review: number;
  size: string[];
  title: string;
}

export default function ItemsPage() {
  const [items, setItems] = useState<Item[]>([]);
  const [loading, setLoading] = useState(true);
  const [categoryFilter, setCategoryFilter] = useState<string>("All");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState<Item | null>(null);
  const [newItem, setNewItem] = useState<Partial<Item>>({
    picUrl: [],
    size: [],
  });
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [sortConfig, setSortConfig] = useState<{ key: keyof Item; direction: "asc" | "desc" } | null>(null);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const itemsPerPage = 5;

  // Fetch items từ API
  useEffect(() => {
    fetchItems();
  }, []);

  const fetchItems = async () => {
    try {
      const response = await fetch("/api/items");
      const data = await response.json();
      setItems(data);
    } catch (error) {
      console.error("Error fetching items:", error);
    } finally {
      setLoading(false);
    }
  };

  // Thêm sản phẩm
  const handleAddItem = async () => {
    try {
      const response = await fetch("/api/items", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newItem),
      });
      if (response.ok) {
        const addedItem = await response.json();
        setItems([...items, addedItem]);
        setNewItem({ picUrl: [], size: [] });
        setIsModalOpen(false);
      }
    } catch (error) {
      console.error("Error adding item:", error);
    }
  };

  // Sửa sản phẩm
  const handleUpdateItem = async () => {
    if (!selectedItem?.id) return;
    try {
      const response = await fetch(`/api/items?id=${selectedItem.id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(selectedItem),
      });
      if (response.ok) {
        const updatedItem = await response.json();
        setItems(items.map((item) => (item.id === updatedItem.id ? updatedItem : item)));
        setIsModalOpen(false);
        setSelectedItem(null);
      }
    } catch (error) {
      console.error("Error updating item:", error);
    }
  };

  // Xóa sản phẩm
  const handleDeleteItem = async (id: string) => {
    if (!confirm("Are you sure you want to delete this item?")) return;
    try {
      const response = await fetch(`/api/items?id=${id}`, {
        method: "DELETE",
      });
      if (response.ok) {
        setItems(items.filter((item) => item.id !== id));
      }
    } catch (error) {
      console.error("Error deleting item:", error);
    }
  };

  // Thêm URL ảnh
  const addPicUrl = () => {
    const currentPicUrl = selectedItem ? selectedItem.picUrl : newItem.picUrl || [];
    if (selectedItem) {
      setSelectedItem({ ...selectedItem, picUrl: [...currentPicUrl, ""] });
    } else {
      setNewItem({ ...newItem, picUrl: [...currentPicUrl, ""] });
    }
  };

  // Cập nhật URL ảnh
  const updatePicUrl = (index: number, value: string) => {
    const currentPicUrl = selectedItem ? selectedItem.picUrl : newItem.picUrl || [];
    const updatedPicUrl = [...currentPicUrl];
    updatedPicUrl[index] = value;
    if (selectedItem) {
      setSelectedItem({ ...selectedItem, picUrl: updatedPicUrl });
    } else {
      setNewItem({ ...newItem, picUrl: updatedPicUrl });
    }
  };

  // Thêm kích thước
  const addSize = () => {
    const currentSize = selectedItem ? selectedItem.size : newItem.size || [];
    if (selectedItem) {
      setSelectedItem({ ...selectedItem, size: [...currentSize, ""] });
    } else {
      setNewItem({ ...newItem, size: [...currentSize, ""] });
    }
  };

  // Cập nhật kích thước
  const updateSize = (index: number, value: string) => {
    const currentSize = selectedItem ? selectedItem.size : newItem.size || [];
    const updatedSize = [...currentSize];
    updatedSize[index] = value;
    if (selectedItem) {
      setSelectedItem({ ...selectedItem, size: updatedSize });
    } else {
      setNewItem({ ...newItem, size: updatedSize });
    }
  };

  // Tìm kiếm sản phẩm
  const filteredItems = items
    .filter((item) => item.category === categoryFilter || categoryFilter === "All")
    .filter((item) =>
      item.title.toLowerCase().includes(searchQuery.toLowerCase())
    );

  // Sắp xếp sản phẩm
  const sortedItems = [...filteredItems];
  if (sortConfig !== null) {
    const key = sortConfig.key;
    const direction = sortConfig.direction;
    sortedItems.sort((a, b) => {
      const valueA = a?.[key] ?? ""; // Nếu không có giá trị thì gán rỗng
      const valueB = b?.[key] ?? "";
    
      if (valueA < valueB) return direction === "asc" ? -1 : 1;
      if (valueA > valueB) return direction === "asc" ? 1 : -1;
      return 0;
    });
  }

  // Phân trang
  const totalItems = sortedItems.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const paginatedItems = sortedItems.slice(startIndex, startIndex + itemsPerPage);

  const handleSort = (key: keyof Item) => {
    let direction: "asc" | "desc" = "asc";
    if (sortConfig && sortConfig.key === key && sortConfig.direction === "asc") {
      direction = "desc";
    }
    setSortConfig({ key, direction });
  };

  const categories = ["All", ...Array.from(new Set(items.map((item) => item.category)))];

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 py-10">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Quản lý sản phẩm</h1>
          <button
            onClick={() => {
              setNewItem({ picUrl: [], size: [] });
              setSelectedItem(null);
              setIsModalOpen(true);
            }}
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
          >
            Thêm sản phẩm mới
          </button>
        </div>

        {/* Bộ lọc danh mục và tìm kiếm */}
        <div className="mb-8 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex flex-wrap gap-2">
            {categories.map((category) => (
              <button
                key={category}
                onClick={() => setCategoryFilter(category)}
                className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                  categoryFilter === category
                    ? "bg-blue-600 text-white"
                    : "bg-white text-gray-700 hover:bg-gray-200"
                }`}
              >
                {category}
              </button>
            ))}
          </div>
          <input
            type="text"
            placeholder="Tìm kiếm theo tên sản phẩm..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="p-2 border rounded-md w-full sm:w-64"
          />
        </div>

        {/* Bảng danh sách sản phẩm */}
        <div className="overflow-x-auto">
          <table className="min-w-full bg-white border border-gray-200 rounded-lg shadow-md">
            <thead>
              <tr className="bg-gray-200 text-gray-700 text-sm uppercase">
                <th className="py-3 px-4 text-left">Ảnh</th>
                <th className="py-3 px-4 text-left">Tên sp</th>
                <th className="py-3 px-4 text-left">Hãng</th>
                <th className="py-3 px-4 text-left cursor-pointer" onClick={() => handleSort("price")}>
                  Giá bán
                  {sortConfig?.key === "price" && (
                    sortConfig.direction === "asc" ? <ChevronUpIcon className="h-4 w-4 inline-block ml-1" /> : <ChevronDownIcon className="h-4 w-4 inline-block ml-1" />
                  )}
                </th>
                <th className="py-3 px-4 text-left">Giá gốc</th>
                <th className="py-3 px-4 text-left">Giảm giá</th>
                <th className="py-3 px-4 text-left cursor-pointer" onClick={() => handleSort("rating")}>
                  Đánh giá
                  {sortConfig?.key === "rating" && (
                    sortConfig.direction === "asc" ? <ChevronUpIcon className="h-4 w-4 inline-block ml-1" /> : <ChevronDownIcon className="h-4 w-4 inline-block ml-1" />
                  )}
                </th>
                <th className="py-3 px-4 text-left">Bình luận</th>
                <th className="py-3 px-4 text-left">Kích cỡ</th>
                <th className="py-3 px-4 text-left">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {paginatedItems.map((item) => (
                <tr key={item.id || item.title} className="border-b hover:bg-gray-50">
                  <td className="py-3 px-4">
                    {item.picUrl[0] ? (
                      <div className="relative w-12 h-12">
                        <Image
                          src={item.picUrl[0]}
                          alt={item.title}
                          fill
                          className="object-cover rounded"
                        />
                      </div>
                    ) : (
                      <span className="text-gray-500">No Image</span>
                    )}
                  </td>
                  <td className="py-3 px-4 text-gray-900">{item.title}</td>
                  <td className="py-3 px-4 text-gray-600">{item.category}</td>
                  <td className="py-3 px-4 text-gray-900">
                    {item.price.toLocaleString("vi-VN")}đ
                  </td>
                  <td className="py-3 px-4 text-gray-500 line-through">
                    {item.oldPrice.toLocaleString("vi-VN")}đ
                  </td>
                  <td className="py-3 px-4 text-red-500">-{item.offPercent}</td>
                  <td className="py-3 px-4">
                    <div className="flex items-center">
                      {[...Array(5)].map((_, i) => (
                        <StarIcon
                          key={i}
                          className={`h-5 w-5 ${
                            i < Math.round(item.rating)
                              ? "text-yellow-400"
                              : "text-gray-300"
                          }`}
                        />
                      ))}
                    </div>
                  </td>
                  <td className="py-3 px-4 text-gray-600">{item.review}</td>
                  <td className="py-3 px-4">
                    <div className="flex flex-wrap gap-1">
                      {item.size.map((size) => (
                        <span
                          key={size}
                          className="text-xs bg-gray-200 text-gray-700 px-2 py-1 rounded"
                        >
                          {size}
                        </span>
                      ))}
                    </div>
                  </td>
                  <td className="py-3 px-4">
                    <div className="flex gap-2">
                      <button
                        onClick={() => {
                          setSelectedItem(item);
                          setIsModalOpen(true);
                        }}
                        className="text-yellow-500 hover:text-yellow-600"
                      >
                        <PencilIcon className="h-5 w-5" />
                      </button>
                      <button
                        onClick={() => handleDeleteItem(item.id!)}
                        className="text-red-500 hover:text-red-600"
                      >
                        <TrashIcon className="h-5 w-5" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Phân trang */}
        {totalItems > itemsPerPage && (
          <div className="mt-6 flex justify-center items-center gap-2">
            <button
              onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
              disabled={currentPage === 1}
              className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md disabled:opacity-50"
            >
              Trang trước
            </button>
            <span className="text-gray-700">
              Trang {currentPage} trên {totalPages}
            </span>
            <button
              onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
              disabled={currentPage === totalPages}
              className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md disabled:opacity-50"
            >
              Trang sau
            </button>
          </div>
        )}

        {/* Nếu không có sản phẩm */}
        {filteredItems.length === 0 && (
          <p className="text-center text-gray-500 mt-10">
            Không có sản phẩm
          </p>
        )}

        {/* Modal thêm/sửa sản phẩm */}
        {isModalOpen && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 overflow-y-auto">
            <div className="bg-white p-6 rounded-lg w-full max-w-lg my-8">
              <h2 className="text-xl font-bold mb-4">
                {selectedItem ? "Edit Item" : "Add New Item"}
              </h2>

              <input
                type="text"
                placeholder="Title"
                value={selectedItem?.title || newItem.title || ""}
                onChange={(e) =>
                  selectedItem
                    ? setSelectedItem({ ...selectedItem, title: e.target.value })
                    : setNewItem({ ...newItem, title: e.target.value })
                }
                className="w-full p-2 mb-4 border rounded"
              />
              <input
                type="text"
                placeholder="Category"
                value={selectedItem?.category || newItem.category || ""}
                onChange={(e) =>
                  selectedItem
                    ? setSelectedItem({ ...selectedItem, category: e.target.value })
                    : setNewItem({ ...newItem, category: e.target.value })
                }
                className="w-full p-2 mb-4 border rounded"
              />
              <textarea
                placeholder="Description"
                value={selectedItem?.description || newItem.description || ""}
                onChange={(e) =>
                  selectedItem
                    ? setSelectedItem({ ...selectedItem, description: e.target.value })
                    : setNewItem({ ...newItem, description: e.target.value })
                }
                className="w-full p-2 mb-4 border rounded"
              />
              <input
                type="number"
                placeholder="Price"
                value={selectedItem?.price || newItem.price || ""}
                onChange={(e) =>
                  selectedItem
                    ? setSelectedItem({ ...selectedItem, price: Number(e.target.value) })
                    : setNewItem({ ...newItem, price: Number(e.target.value) })
                }
                className="w-full p-2 mb-4 border rounded"
              />
              <input
                type="number"
                placeholder="Old Price"
                value={selectedItem?.oldPrice || newItem.oldPrice || ""}
                onChange={(e) =>
                  selectedItem
                    ? setSelectedItem({ ...selectedItem, oldPrice: Number(e.target.value) })
                    : setNewItem({ ...newItem, oldPrice: Number(e.target.value) })
                }
                className="w-full p-2 mb-4 border rounded"
              />
              <input
                type="text"
                placeholder="Discount Percent (e.g., 35%)"
                value={selectedItem?.offPercent || newItem.offPercent || ""}
                onChange={(e) =>
                  selectedItem
                    ? setSelectedItem({ ...selectedItem, offPercent: e.target.value })
                    : setNewItem({ ...newItem, offPercent: e.target.value })
                }
                className="w-full p-2 mb-4 border rounded"
              />
              <input
                type="number"
                placeholder="Rating (1-5)"
                min="1"
                max="5"
                value={selectedItem?.rating || newItem.rating || ""}
                onChange={(e) =>
                  selectedItem
                    ? setSelectedItem({ ...selectedItem, rating: Number(e.target.value) })
                    : setNewItem({ ...newItem, rating: Number(e.target.value) })
                }
                className="w-full p-2 mb-4 border rounded"
              />
              <input
                type="number"
                placeholder="Number of Reviews"
                value={selectedItem?.review || newItem.review || ""}
                onChange={(e) =>
                  selectedItem
                    ? setSelectedItem({ ...selectedItem, review: Number(e.target.value) })
                    : setNewItem({ ...newItem, review: Number(e.target.value) })
                }
                className="w-full p-2 mb-4 border rounded"
              />

              {/* Danh sách URL ảnh */}
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Image URLs
                </label>
                {(selectedItem ? selectedItem.picUrl : newItem.picUrl || []).map(
                  (url, index) => (
                    <input
                      key={index}
                      type="text"
                      placeholder={`Image URL ${index + 1}`}
                      value={url}
                      onChange={(e) => updatePicUrl(index, e.target.value)}
                      className="w-full p-2 mb-2 border rounded"
                    />
                  )
                )}
                <button
                  onClick={addPicUrl}
                  className="text-blue-600 hover:underline text-sm"
                >
                  + Thêm Image URL
                </button>
              </div>

              {/* Danh sách kích thước */}
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Kích cỡ
                </label>
                {(selectedItem ? selectedItem.size : newItem.size || []).map(
                  (size, index) => (
                    <input
                      key={index}
                      type="text"
                      placeholder={`Size ${index + 1}`}
                      value={size}
                      onChange={(e) => updateSize(index, e.target.value)}
                      className="w-full p-2 mb-2 border rounded"
                    />
                  )
                )}
                <button
                  onClick={addSize}
                  className="text-blue-600 hover:underline text-sm"
                >
                  + Thêm kích thước
                </button>
              </div>

              <div className="flex gap-2">
                <button
                  onClick={selectedItem ? handleUpdateItem : handleAddItem}
                  className="flex-1 bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700"
                >
                  {selectedItem ? "Cập nhật" : "Thêm"}
                </button>
                <button
                  onClick={() => setIsModalOpen(false)}
                  className="flex-1 bg-gray-300 text-gray-700 py-2 rounded-md hover:bg-gray-400"
                >
                  Huỷ
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}